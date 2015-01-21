package com.ujm.xmltech.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

//http://loianegroner.com/2011/06/jaxb-custom-binding-java-util-date-spring-3-serialization/
public class JaxbDateAdapter extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public String marshal(Date date) throws Exception {
		return dateFormat.format(date);
	}

	@Override
	public Date unmarshal(String date) throws Exception {
		return dateFormat.parse(date);
	}
        
        public String marshalv2(Date date) throws Exception {
		return dateFormat2.format(date);
	}

	
	public Date unmarshalv2(String date) throws Exception {
		return dateFormat2.parse(date);
	}
}