package org.eclipse.vtp.desktop.model.legacy.v4_0To5_0;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature5_0;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("deprecation")
public class ProjectConverter
{

	public ProjectConverter()
	{
	}

	public void convertProject(IProject project)
	{
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			if(project.hasNature(WorkflowProjectNature.NATURE_ID) ||
			   project.hasNature("org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowProjectNature"))
			{
				//update the build path document
				IFile buildPath = project.getFile(".buildPath");
				Document buildPathDocument = builder.parse(buildPath.getContents());
				
				Element rootElement = buildPathDocument.getDocumentElement();
				Element aspectsElement = rootElement.getOwnerDocument().createElement("project-aspects");
				rootElement.appendChild(aspectsElement);
				NodeList brandsElementList = rootElement.getElementsByTagName("brands");
				Element brandsElement = (Element)brandsElementList.item(0);
				Element typeSupportElement = null;
				NodeList typeSupportElementList = rootElement.getElementsByTagName("interaction-type-support");
				if(typeSupportElementList.getLength() > 0)
					typeSupportElement = (Element)typeSupportElementList.item(0);
				Element mediaDefaultsElement = null;
				NodeList mediaDefaultsElementList = rootElement.getElementsByTagName("media-defaults");
				if(mediaDefaultsElementList.getLength() > 0)
					mediaDefaultsElement = (Element)mediaDefaultsElementList.item(0);
				//remove all the old things to eliminate blank lines
				NodeList allChildren = rootElement.getChildNodes();
				for(int i = 0; i < allChildren.getLength(); i++)
				{
					rootElement.removeChild(allChildren.item(i));
				}

				Element dependenciesAspectElement = buildPathDocument.createElement("aspect");
				dependenciesAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.dependencies");
				aspectsElement.appendChild(dependenciesAspectElement);
				Element businessObjectAspectElement = buildPathDocument.createElement("aspect");
				businessObjectAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.businessobjects");
				aspectsElement.appendChild(businessObjectAspectElement);
				Element workflowAspectElement = buildPathDocument.createElement("aspect");
				workflowAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.workflow");
				aspectsElement.appendChild(workflowAspectElement);
				Element brandAspectElement = buildPathDocument.createElement("aspect");
				aspectsElement.appendChild(brandAspectElement);
				brandAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.branding");
				brandAspectElement.appendChild(brandsElement);
				if(typeSupportElement != null)
				{
					Element languageAspectElement = buildPathDocument.createElement("aspect");
					languageAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.languagesupport");
					aspectsElement.appendChild(languageAspectElement);
					languageAspectElement.appendChild(typeSupportElement);
				}
				if(mediaDefaultsElement != null)
				{
					Element interactiveAspectElement = buildPathDocument.createElement("aspect");
					interactiveAspectElement.setAttribute("id", "com.openmethods.openvxml.desktop.model.aspect.interactive");
					aspectsElement.appendChild(interactiveAspectElement);
					interactiveAspectElement.appendChild(mediaDefaultsElement);
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransformerFactory transfactory = TransformerFactory.newInstance();
				Transformer t = transfactory.newTransformer();
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(buildPathDocument), new XMLWriter(baos).toXMLResult());
				buildPath.setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);

				IProjectDescription description = project.getDescription();
				description.setNatureIds(new String[] {WorkflowProjectNature5_0.NATURE_ID});
				project.setDescription(description, null);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
