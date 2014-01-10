package fi.om.initiative.service;

import java.util.List;

public interface StatusService {

    List<StatusServiceImpl.KeyValueInfo> getSystemInfo();

    List<StatusServiceImpl.KeyValueInfo> getSchemaVersionInfo();

    List<StatusServiceImpl.KeyValueInfo> getApplicationInfo();

    List<StatusServiceImpl.KeyValueInfo> getConfigurationInfo();

    List<StatusServiceImpl.KeyValueInfo> getConfigurationTestInfo();

    List<StatusServiceImpl.KeyValueInfo> getInvalidHelpUris();
}