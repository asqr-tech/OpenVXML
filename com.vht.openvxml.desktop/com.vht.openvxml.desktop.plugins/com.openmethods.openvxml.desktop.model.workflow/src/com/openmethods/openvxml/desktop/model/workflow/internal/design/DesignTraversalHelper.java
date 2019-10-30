package com.openmethods.openvxml.desktop.model.workflow.internal.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConnector;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.IExitBroadcastReceiver;

public class DesignTraversalHelper {

	public static <T> List<T> getDesignElements(Design design, Class<T> type) {
		List<T> ret = new ArrayList<T>();
		for (IDesignElement designElement : design.getDesignElements()) {
			@SuppressWarnings("unchecked")
			T object = (T) designElement.getAdapter(type);
			if (object != null) {
				ret.add(object);
			}
		}
		return ret;
	}

	public static <T> List<T> getUpStreamDesignElements(
			IDesignElement startElement, Class<T> destinationType) {
		List<T> ret = new ArrayList<T>();
		Map<String, IDesignElement> visited = new HashMap<String, IDesignElement>();
		visited.put(startElement.getId(), startElement);
		getUpStreamDesignElements0(startElement, destinationType, ret, visited);
		return ret;
	}

	private static <T> void getUpStreamDesignElements0(
			IDesignElement startElement, Class<T> destinationType,
			List<T> elements, Map<String, IDesignElement> visited) {
		List<IDesignConnector> connectors = startElement
				.getIncomingConnectors();
		for (IDesignConnector connector : connectors) {

			IDesignElement sourceElement = connector.getOrigin();
			if (connector.getOrigin().getConnectorRecords().isEmpty()) {
				final String projectName = connector.getDesign().getDocument()
						.getProject().getName();
				final String documentName = connector.getDesign().getName();
				final String sourceName = connector.getOrigin().getName();
				final String destinationName = connector.getDestination()
						.getName();
				final String connectorId = connector.getId();
				System.err.println("Found empty connector in \r\n"
						+ "Project: " + projectName + "\r\n" + "Document: "
						+ documentName + "\r\n" + "Source element: "
						+ sourceName + "\r\n" + "Destination element: "
						+ destinationName + "\r\n" + "ID: " + connectorId);
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						Shell shell = Display.getCurrent().getActiveShell();
						MessageBox mbox = new MessageBox(shell);
						mbox.setText("Warning - Empty Connector");
						mbox.setMessage("Found empty connector in \r\n"
								+ "Project: " + projectName + "\r\n"
								+ "Document: " + documentName + "\r\n"
								+ "Source element: " + sourceName + "\r\n"
								+ "Destination element: " + destinationName
								+ "\r\n" + "ID: " + connectorId);
						mbox.open();
					}
				});
			}
			@SuppressWarnings("unchecked")
			T adapter = (T) sourceElement.getAdapter(destinationType);
			if (adapter != null) {
				elements.add(adapter);
			}
			if (visited.get(sourceElement.getId()) == null) {
				visited.put(sourceElement.getId(), sourceElement);
				getUpStreamDesignElements0(sourceElement, destinationType,
						elements, visited);
			}
		}
		if (startElement.getDesign() == startElement.getDesign().getDocument()
				.getMainDesign()) {
			List<IExitBroadcastReceiver> receivers = startElement
					.getExitBroadcastReceivers();
			if (!receivers.isEmpty()) {
				for (IDesignElement sourceElement : startElement.getDesign()
						.getDesignElements()) {
					for (IDesignElementConnectionPoint point : sourceElement
							.getConnectorRecords()) {
						if (point.getDesignConnector() == null) {
							for (IExitBroadcastReceiver receiver : receivers) {
								if (point.getName().equals(
										receiver.getExitPattern())) {
									@SuppressWarnings("unchecked")
									T adapter = (T) sourceElement
											.getAdapter(destinationType);
									if (adapter != null) {
										elements.add(adapter);
									}
									if (visited.get(sourceElement.getId()) == null) {
										visited.put(sourceElement.getId(),
												sourceElement);
										getUpStreamDesignElements0(
												sourceElement, destinationType,
												elements, visited);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static <T> List<T> getDownStreamDesignElements(
			IDesignElement startElement, Class<T> destinationType) {
		List<T> ret = new ArrayList<T>();
		Map<String, IDesignElement> visited = new HashMap<String, IDesignElement>();
		visited.put(startElement.getId(), startElement);
		getDownStreamDesignElements0(startElement, destinationType, ret,
				visited);
		return ret;
	}

	private static <T> void getDownStreamDesignElements0(
			IDesignElement startElement, Class<T> destinationType,
			List<T> elements, Map<String, IDesignElement> visited) {
		List<IDesignElementConnectionPoint> records = startElement
				.getConnectorRecords();
		for (IDesignElementConnectionPoint connectionPoint : records) {
			IDesignConnector connector = connectionPoint.getDesignConnector();
			if (connector != null) {
				IDesignElement targetElement = connector.getDestination();
				@SuppressWarnings("unchecked")
				T adapter = (T) targetElement.getAdapter(destinationType);
				if (adapter != null) {
					elements.add(adapter);
				}
				if (visited.get(targetElement.getId()) == null) {
					visited.put(targetElement.getId(), targetElement);
					getDownStreamDesignElements0(targetElement,
							destinationType, elements, visited);
				}
			} else {
				if (startElement.getDesign() == startElement.getDesign()
						.getDocument().getMainDesign()) {
					for (IDesignElement targetElement : startElement
							.getDesign().getDesignElements()) {
						for (IExitBroadcastReceiver receiver : targetElement
								.getExitBroadcastReceivers()) {
							if (connectionPoint.getName().equals(
									receiver.getExitPattern())) {
								@SuppressWarnings("unchecked")
								T adapter = (T) targetElement
										.getAdapter(destinationType);
								if (adapter != null) {
									elements.add(adapter);
								}
								if (visited.get(targetElement.getId()) == null) {
									visited.put(targetElement.getId(),
											targetElement);
									getDownStreamDesignElements0(targetElement,
											destinationType, elements, visited);
								}
								break;
							}
						}
					}
				}
			}
		}
	}
}
