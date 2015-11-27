package org.openmrs.module.owa.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/module/owa/settings")
public class SettingsFormController {
	
	private static final Log log = LogFactory.getLog(SettingsFormController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm() {
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(@ModelAttribute("globalPropertiesModel") GlobalPropertiesModel globalPropertiesModel,
	        Errors errors, WebRequest request) {
		if (Context.hasPrivilege("Manage OWA")) {
			globalPropertiesModel.validate(globalPropertiesModel, errors);
			if (errors.hasErrors())
				return null; // show the form again
				
			AdministrationService administrationService = Context.getAdministrationService();
			for (GlobalProperty p : globalPropertiesModel.getProperties()) {
				administrationService.saveGlobalProperty(p);
			}
			
			request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, Context.getMessageSourceService()
			        .getMessage("general.saved"), WebRequest.SCOPE_SESSION);
		}
		return "redirect:settings.form";
	}
	
	/**
	 * @return
	 */
	@ModelAttribute("globalPropertiesModel")
	public GlobalPropertiesModel getModel() {
		List<GlobalProperty> editableProps = new ArrayList<>();
		if(Context.hasPrivilege("Manage OWA")){
                        editableProps.add(Context.getAdministrationService().getGlobalPropertyObject("owa.appBaseUrl"));
                        editableProps.add(Context.getAdministrationService().getGlobalPropertyObject("owa.appFolderPath"));
                        editableProps.add(Context.getAdministrationService().getGlobalPropertyObject("owa.appStoreUrl"));
                }
		return new GlobalPropertiesModel(editableProps);
	}
	
	/**
	 * Represents the model object for the form, which is typically used as a wrapper for the list
	 * of global properties list so that spring can bind the properties of the objects in the list.
	 * Also capable of validating itself
	 */
	public class GlobalPropertiesModel implements Validator {
		
		private List<GlobalProperty> properties;
		
		public GlobalPropertiesModel() {
		}
		
		public GlobalPropertiesModel(List<GlobalProperty> properties) {
			this.properties = properties;
		}
		
		/**
		 * @param clazz
		 * @return
		 * @see org.springframework.validation.Validator#supports(java.lang.Class)
		 */
		@Override
		public boolean supports(Class<?> clazz) {
			return clazz.equals(getClass());
		}
		
		/**
		 * @param target
		 * @param errors
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
		}
		
		/**
		 * Returns the global property for the given propertyName
		 * 
		 * @param propertyName
		 * @return
		 */
		public GlobalProperty getProperty(String propertyName) {
			GlobalProperty prop = null;
			for (GlobalProperty gp : getProperties()) {
				if (gp.getProperty().equals(propertyName)) {
					prop = gp;
					break;
				}
			}
			return prop;
		}
		
		/**
		 * @return
		 */
		public List<GlobalProperty> getProperties() {
			return properties;
		}
		
		/**
		 * @param properties
		 */
		public void setProperties(List<GlobalProperty> properties) {
			this.properties = properties;
		}
	}
}