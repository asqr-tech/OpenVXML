package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.controller.IController;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SubmitConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.conversation.IEndMessage;

/**
 * FinalAction.
 * 
 * @author Lonnie Pryor
 */
// public class SubmitAction extends ExitAction
public class SubmitAction implements IAction {
	/** The context that contains this action. */
	protected final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configurations to use. */
	protected final AssignmentConfiguration[] configurations;
	private final SubmitConfiguration submitConfiguration;

	/**
	 * Creates a new FinalAction.
	 * 
	 * @param context
	 * @param controller
	 * @param configuration
	 */
	public SubmitAction(IActionContext context, IController controller,
			SubmitConfiguration submitConfiguration,
			IConversation conversation, AssignmentConfiguration[] configurations) {
		this.context = context;
		this.conversation = conversation;
		this.configurations = configurations;
		this.submitConfiguration = submitConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.actions.ExitAction#execute()
	 */
	@Override
	public IActionResult execute() {
		IEndMessage endMessage = conversation.createEndMessage(configurations);
		MediaConfiguration mediaConfig = submitConfiguration
				.getMediaConfiguration();
		endMessage.setVariableValue("*submit_url", conversation
				.resolveProperty(mediaConfig.getPropertyConfiguration("url"),
						false, false));
		endMessage.setVariableValue("*submit_method", conversation
				.resolveProperty(
						mediaConfig.getPropertyConfiguration("method"), false,
						false));
		endMessage.setVariableValue("*submit_isSubmit", "true");

		List<String> inputNames = new ArrayList<String>();
		inputNames.addAll(Arrays.asList(submitConfiguration.getInputNames()));
		for (String name : inputNames) {
			if (submitConfiguration.isInputVariable(name)) {
				endMessage.setVariableValue("*submit_inputVariable", name + ":"
						+ submitConfiguration.getInputValue(name));
			} else {
				endMessage.setVariableValue(name,
						submitConfiguration.getInputValue(name));
			}
		}

		endMessage.enqueue();

		return context.createResult(IActionResult.RESULT_NAME_ABORT);
	}
}
