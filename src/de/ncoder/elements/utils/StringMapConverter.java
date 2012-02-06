package de.ncoder.elements.utils;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class StringMapConverter<T> extends MapConverter {
	private final String attributename;

	public StringMapConverter(Mapper mapper, String attributename) {
		super(mapper);
		this.attributename = attributename;
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type == HashMap.class;
	}

	@SuppressWarnings("unchecked")
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map<String, T> map = (Map<String, T>) source;
		for (Map.Entry<String, T> entry : map.entrySet()) {
			T value = entry.getValue();
			writer.startNode(mapper().serializedClass(value.getClass()));
			writer.addAttribute(attributename, entry.getKey());
			context.convertAnother(value);
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, T> map = new HashMap<String, T>();
		populateStringMap(reader, context, map);
		return map;
	}

	@SuppressWarnings("unchecked")
	protected void populateStringMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map<String, T> map) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getAttribute(attributename);
			if (key == null || key.isEmpty()) {
				key = (String) readItem(reader, context, map);
			}
			T value = (T) readItem(reader, context, map);
			reader.moveUp();
			map.put(key, value);
		}
	}
}