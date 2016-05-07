package com.oneliang.android.common.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oneliang.frame.jdbc.MappingBean;
import com.oneliang.frame.jdbc.MappingColumnBean;
import com.oneliang.frame.jdbc.MappingContext;
import com.oneliang.exception.InitializeException;
import com.oneliang.util.common.JavaXmlUtil;

import android.content.res.AssetManager;

public class AndroidMappingContext extends MappingContext implements AndroidAssetManager{

    private AssetManager assetManager=null;

    public void initialize(final String parameters){
        String path=parameters;
        if(this.assetManager!=null){
        	try{
	            InputStream inputStream=this.assetManager.open(path);
	            Document document=JavaXmlUtil.parse(inputStream);
	            Element root=document.getDocumentElement();
	            NodeList beanElementList=root.getElementsByTagName(MappingBean.TAG_BEAN);
	            if(beanElementList!=null){
	                int length=beanElementList.getLength();
	                for(int index=0;index<length;index++){
	                    Node beanElement=beanElementList.item(index);
	                    MappingBean mappingBean=new MappingBean();
	                    NamedNodeMap attributeMap=beanElement.getAttributes();
	                    JavaXmlUtil.initializeFromAttributeMap(mappingBean, attributeMap);
	                    //bean column
	                    NodeList childNodeList=beanElement.getChildNodes();
	                    if(childNodeList!=null){
	                        int childNodeLength=childNodeList.getLength();
	                        for(int childNodeIndex=0;childNodeIndex<childNodeLength;childNodeIndex++){
	                            Node childNode=childNodeList.item(childNodeIndex);
	                            String nodeName=childNode.getNodeName();
	                            if(nodeName.equals(MappingColumnBean.TAG_COLUMN)){
	                                MappingColumnBean mappingColumnBean=new MappingColumnBean();
	                                NamedNodeMap childNodeAttributeMap=childNode.getAttributes();
	                                JavaXmlUtil.initializeFromAttributeMap(mappingColumnBean, childNodeAttributeMap);
	                                mappingBean.addMappingColumnBean(mappingColumnBean);
	                            }
	                        }
	                    }
	                    String className=mappingBean.getType();
	                    classNameMappingBeanMap.put(className, mappingBean);
	                    simpleNameMappingBeanMap.put(Class.forName(className).getSimpleName(), mappingBean);
	                }
	            }
	            inputStream.close();
        	}catch (Exception e) {
        		throw new InitializeException(parameters, e);
			}
        }
    }

    /**
     * @param assetManager the assetManager to set
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
}
