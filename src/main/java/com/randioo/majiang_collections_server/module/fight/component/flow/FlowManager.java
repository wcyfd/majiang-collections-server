package com.randioo.majiang_collections_server.module.fight.component.flow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.randioo.randioo_server_base.config.XmlReader;

/**
 * 流管理器
 * 
 * @author wcy 2017年8月24日
 *
 */
public class FlowManager implements XmlReader {

    @Override
    public void readXml(InputStream arg0) {
        SAXReader sax = new SAXReader();
        try {
            Document doc = sax.read(arg0);
            Element element = doc.getRootElement();
            Iterator<Element> it = element.elements().iterator();
            while (it.hasNext()) {
                Element e1 = it.next();
                String name = element.getName();
                System.out.println(e1.getName());
            }
            String str = element.getUniquePath();

            System.out.println(str);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void addFlow(Flow flow) {
    }

    public Flow newFlow(Class<? extends Flow> flowClass) {
        return null;
    }

    public static void main(String[] args) {
        FlowManager m = new FlowManager();
        Flow flow1 = createProcessFlow();
        Flow flow2 = createProcessFlow();
        Flow flow3 = createConditionFlow();

        m.addFlow(flow1);
        m.addFlow(flow2);
        m.addFlow(flow3);
    }

    private static Flow createProcessFlow() {
        return null;
    }

    private static Flow createConditionFlow() {
        return null;
    }
}
