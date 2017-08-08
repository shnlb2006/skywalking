package org.skywalking.apm.collector.agentstream.worker.service.entry.dao;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.skywalking.apm.collector.agentstream.worker.service.entry.define.ServiceEntryTable;
import org.skywalking.apm.collector.storage.elasticsearch.dao.EsDAO;
import org.skywalking.apm.collector.stream.worker.impl.dao.IPersistenceDAO;
import org.skywalking.apm.collector.stream.worker.impl.data.Data;
import org.skywalking.apm.collector.stream.worker.impl.data.DataDefine;

/**
 * @author pengys5
 */
public class ServiceEntryEsDAO extends EsDAO implements IServiceEntryDAO, IPersistenceDAO<IndexRequestBuilder, UpdateRequestBuilder> {

    @Override public Data get(String id, DataDefine dataDefine) {
        GetResponse getResponse = getClient().prepareGet(ServiceEntryTable.TABLE, id).get();
        if (getResponse.isExists()) {
            Data data = dataDefine.build(id);
            Map<String, Object> source = getResponse.getSource();
            data.setDataInteger(0, (Integer)source.get(ServiceEntryTable.COLUMN_APPLICATION_ID));
            data.setDataString(1, (String)source.get(ServiceEntryTable.COLUMN_AGG));
            data.setDataLong(0, (Long)source.get(ServiceEntryTable.COLUMN_TIME_BUCKET));
            return data;
        } else {
            return null;
        }
    }

    @Override public IndexRequestBuilder prepareBatchInsert(Data data) {
        Map<String, Object> source = new HashMap<>();
        source.put(ServiceEntryTable.COLUMN_APPLICATION_ID, data.getDataInteger(0));
        source.put(ServiceEntryTable.COLUMN_AGG, data.getDataString(1));
        source.put(ServiceEntryTable.COLUMN_TIME_BUCKET, data.getDataLong(0));

        return getClient().prepareIndex(ServiceEntryTable.TABLE, data.getDataString(0)).setSource(source);
    }

    @Override public UpdateRequestBuilder prepareBatchUpdate(Data data) {
        Map<String, Object> source = new HashMap<>();
        source.put(ServiceEntryTable.COLUMN_APPLICATION_ID, data.getDataInteger(0));
        source.put(ServiceEntryTable.COLUMN_AGG, data.getDataString(1));
        source.put(ServiceEntryTable.COLUMN_TIME_BUCKET, data.getDataLong(0));

        return getClient().prepareUpdate(ServiceEntryTable.TABLE, data.getDataString(0)).setDoc(source);
    }
}
