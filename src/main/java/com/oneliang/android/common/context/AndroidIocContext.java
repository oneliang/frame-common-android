package com.oneliang.android.common.context;

import java.io.InputStream;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oneliang.frame.ioc.IocAfterInjectBean;
import com.oneliang.frame.ioc.IocBean;
import com.oneliang.frame.ioc.IocConfigurationBean;
import com.oneliang.frame.ioc.IocConstructorBean;
import com.oneliang.frame.ioc.IocContext;
import com.oneliang.frame.ioc.IocPropertyBean;
import com.oneliang.exception.InitializeException;
import com.oneliang.util.common.JavaXmlUtil;

import android.content.res.AssetManager;

public class AndroidIocContext extends IocContext implements AndroidAssetManager{

	private AssetManager assetManager=null;
	
	public void initialize(final String parameters){
		String path=parameters;
		if(this.assetManager!=null){
			try{
				InputStream inputStream=this.assetManager.open(path);
				Document document=JavaXmlUtil.parse(inputStream);
				Element root=document.getDocumentElement();
				//configuration
				NodeList configurationElementList=root.getElementsByTagName(IocConfigurationBean.TAG_CONFIGURATION);
				if(configurationElementList!=null&&configurationElementList.getLength()>0){
					NamedNodeMap configurationAttributeMap=configurationElementList.item(0).getAttributes();
					JavaXmlUtil.initializeFromAttributeMap(iocConfigurationBean, configurationAttributeMap);
				}
				//ioc bean
				NodeList beanElementList=root.getElementsByTagName(IocBean.TAG_BEAN);
				//xml to object
				if(beanElementList!=null){
					int beanElementLength=beanElementList.getLength();
					for(int index=0;index<beanElementLength;index++){
						Node beanElement=beanElementList.item(index);
						//bean
						IocBean iocBean=new IocBean();
						NamedNodeMap attributeMap=beanElement.getAttributes();
						JavaXmlUtil.initializeFromAttributeMap(iocBean, attributeMap);
						
						//constructor
						NodeList childNodeList=beanElement.getChildNodes();
						if(childNodeList!=null){
							int childNodeLength=childNodeList.getLength();
							for(int childNodeIndex=0;childNodeIndex<childNodeLength;childNodeIndex++){
								Node childNode=childNodeList.item(childNodeIndex);
								String nodeName=childNode.getNodeName();
								if(nodeName.equals(IocConstructorBean.TAG_CONSTRUCTOR)){
									IocConstructorBean iocConstructorBean=new IocConstructorBean();
									NamedNodeMap iocConstructorAttributeMap=childNode.getAttributes();
									JavaXmlUtil.initializeFromAttributeMap(iocConstructorBean, iocConstructorAttributeMap);
									iocBean.setIocConstructorBean(iocConstructorBean);
								}
								//property
								else if(nodeName.equals(IocPropertyBean.TAG_PROPERTY)){
									IocPropertyBean iocPropertyBean=new IocPropertyBean();
									NamedNodeMap iocPropertyAttributeMap=childNode.getAttributes();
									JavaXmlUtil.initializeFromAttributeMap(iocPropertyBean, iocPropertyAttributeMap);
									iocBean.addIocPropertyBean(iocPropertyBean);
								}
								//after inject
								else if(nodeName.equals(IocAfterInjectBean.TAG_AFTER_INJECT)){
									IocAfterInjectBean iocAfterInjectBean=new IocAfterInjectBean();
									NamedNodeMap iocAfterInjectAttributeMap=childNode.getAttributes();
									JavaXmlUtil.initializeFromAttributeMap(iocAfterInjectBean, iocAfterInjectAttributeMap);
									iocBean.addIocAfterInjectBean(iocAfterInjectBean);
								}
							}
						}
						if(!iocBeanMap.containsKey(iocBean.getId())){
							iocBeanMap.put(iocBean.getId(),iocBean);
						}
					}
				}
				inputStream.close();
			}catch (Exception e) {
				throw new InitializeException(parameters, e);
			}
		}
	}

	/**
	 * @param id
	 * @param object
	 * @throws Exception
	 */
	public void autoInjectObjectById(String id,Object object) throws Exception {
	    objectMap.put(id, object);
		super.autoInjectObjectById(object);
	}

	/**
	 * @param assetManager the assetManager to set
	 */
	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	/**
	 * get object map
	 * @return Map<String,Object>
	 */
	public Map<String,Object> getObjectMap(){
	    return objectMap;
	}

	/**
	 * get ioc bean map
	 * @return Map<String,IocBean>
	 */
	public Map<String,IocBean> getIocBeanMap(){
	    return iocBeanMap;
	}
}
