package twins.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import twins.data.dao.ItemsDao;
import twins.logic.converters.ItemsEntityConverterImplementation;
import twins.logic.converters.OpertionsEntityConverterImplementation;
import twins.utils.AttributeNames;
import twins.utils.Utils;

@Component
public class AsyncReportHandler {

	private ObjectMapper jackson;
	private ItemsEntityConverterImplementation itemEntityConverter;
	private ItemsDao itemDao;
	private Utils utils;

	public AsyncReportHandler() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setItemEntityConverter(ItemsEntityConverterImplementation itemEntityConverter) {
		this.itemEntityConverter = itemEntityConverter;
	}


	@Autowired
	public void setMessageDao(ItemsDao itemDao) {
		this.itemDao = itemDao;
	}

	@Transactional
	@JmsListener(destination = "ReportInbox") // receive messages from MOM
	public void handleJson(String json) {
		try {
			OperationEntity opEntity = this.jackson.readValue(json, OperationEntity.class);
			Map<String, Object> attributes = itemEntityConverter.JSONToMap(opEntity.getOperationAttributes());
			Map<String, Object> itemAttributes = new HashMap<>();
			
			String reportJson = (String) attributes.get(AttributeNames.MESSAGE);
			String title = (String) attributes.get(AttributeNames.TITLE);

			ItemEntity entity = new ItemEntity();
			String id = utils.createUniqueID(opEntity.getCreatorSpace());
			entity.setCreatorEmail(opEntity.getCreatorEmail());
			itemAttributes.put(AttributeNames.MESSAGE, reportJson);
			entity.setItemAttributes(itemEntityConverter.mapToJSON(itemAttributes));	
			entity.setItemId(id);
			entity.setItemType(ItemType.REPORT.name());
			entity.setItemName(title);
			entity.setActive(true);
			entity.setCreatedTimestamp(new Date());
			this.itemDao.save(entity);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
