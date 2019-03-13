package com.wsf.core.xsd;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class RpcBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    private Class<?> pojoClass;

    public RpcBeanDefinitionParser(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return pojoClass;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr attr = (Attr) attrs.item(i);
            if (isEligibleAttribute(attr, parserContext)) {
                String propertyName = extractPropertyName(attr.getLocalName());
                Assert.state(StringUtils.hasText(propertyName), "Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
                if ("interface".equals(propertyName)) {
                    propertyName = "interfaceName";
                }
                builder.addPropertyValue(underLineToCamel(propertyName), attr.getValue());
            }
        }
        postProcess(builder, element);
    }

    /**
     * 下划线字符串转驼峰字符串
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    private String underLineToCamel(String str) {
        String[] arr = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                sb.append(arr[i]);
            } else {
                sb.append(firstToUpperCase(arr[i]));
            }
        }

        return sb.toString();
    }

    /**
     * 字符串首字母变大写
     *
     * @param str 字符串
     * @return 首字母变为大写之后的字符串
     */
    private String firstToUpperCase(String str) {
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
