package com.oneliang.android.common.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oneliang.frame.Context;
import com.oneliang.frame.configuration.ConfigurationBean;
import com.oneliang.frame.configuration.ConfigurationContext;
import com.oneliang.exception.InitializeException;
import com.oneliang.util.common.JavaXmlUtil;

import android.content.res.AssetManager;

public class AndroidConfigurationContext extends ConfigurationContext implements AndroidAssetManager{

	private AssetManager assetManager=null;

	public void initialize(final String parameters){
		String path=parameters;
		if(this.assetManager!=null){
			try{
				InputStream inputStream=this.assetManager.open(path);
				Document document=JavaXmlUtil.parse(inputStream);
				Element root=document.getDocumentElement();
				NodeList configurationList=root.getElementsByTagName(ConfigurationBean.TAG_CONFIGURATION);
				if(configurationList!=null){
					int length=configurationList.getLength();
					for(int index=0;index<length;index++){
						ConfigurationBean configurationBean=new ConfigurationBean();
						Node configurationNode=configurationList.item(index);
						NamedNodeMap configurationAttributesMap=configurationNode.getAttributes();
						JavaXmlUtil.initializeFromAttributeMap(configurationBean,configurationAttributesMap);
						Context context=(Context)(Class.forName(configurationBean.getContextClass()).newInstance());
						if(context instanceof AndroidAssetManager){
							((AndroidAssetManager)context).setAssetManager(this.assetManager);
						}
						context.initialize(configurationBean.getParameters());
						configurationBean.setContextInstance(context);
						configurationBeanMap.put(configurationBean.getId(),configurationBean);
						this.selfConfigurationBeanMap.put(configurationBean.getId(),configurationBean);
					}
				}
				inputStream.close();
			}catch (Exception e) {
				throw new InitializeException(parameters, e);
			}
		}
		this.initialized=true;
	}

	/**
	 * @param assetManager the assetManager to set
	 */
	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
}
