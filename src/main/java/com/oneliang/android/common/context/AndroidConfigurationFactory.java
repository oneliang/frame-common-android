package com.oneliang.android.common.context;

import java.util.Iterator;
import java.util.Map.Entry;

import com.oneliang.frame.ConfigurationFactory;
import com.oneliang.frame.Context;
import com.oneliang.frame.configuration.ConfigurationBean;

public final class AndroidConfigurationFactory extends ConfigurationFactory{

	static {
		configurationContext=new AndroidConfigurationContext();
	}

    /**
	 * @Title: getSingletonAndroidConfigurationContext
	 * @return AndroidConfigurationContext
	 */
	public static AndroidConfigurationContext getSingletonConfigurationContext(){
		return (AndroidConfigurationContext)configurationContext;
	}

    /**
     * clean the map which in cache.
     */
    public static void cleanMap(){
        Iterator<Entry<String,ConfigurationBean>> iterator=configurationContext.getConfigurationBeanEntrySet().iterator();
        while(iterator.hasNext()){
            Entry<String,ConfigurationBean> entry=iterator.next();
            ConfigurationBean configurationBean=entry.getValue();
            Context context=configurationBean.getContextInstance();
            if(context instanceof AndroidIocContext){
                AndroidIocContext androidIocContext=(AndroidIocContext)context;
                androidIocContext.getIocBeanMap().clear();
                androidIocContext.getObjectMap().clear();
                break;
            }
        }
    }
}
