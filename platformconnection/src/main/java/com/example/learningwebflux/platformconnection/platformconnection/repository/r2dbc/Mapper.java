package com.example.learningwebflux.platformconnection.platformconnection.repository.r2dbc;

import com.example.learningwebflux.platformconnection.Platform;
import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import com.example.learningwebflux.platformconnection.r2dbc.datamodels.PlatformConnectionDataModel;
import org.springframework.stereotype.Component;

@Component
class Mapper {
    public PlatformConnectionDataModel toDataModel(PlatformConnection domainModel) {
        return new PlatformConnectionDataModel(
            domainModel.id,
            domainModel.userId,
            domainModel.platform.toString(),
            domainModel.platformUserId,
            domainModel.getRefreshToken(),
            domainModel.getAccessToken(),
            domainModel.getExpiresAt()
        );
    }

    public PlatformConnection toDomainModel(PlatformConnectionDataModel dataModel) {
        return new PlatformConnection(
            dataModel.id,
            dataModel.userId,
            Platform.valueOf(dataModel.platform),
            dataModel.platformUserId,
            dataModel.refreshToken,
            dataModel.accessToken,
            dataModel.expiresAt
        );
    }
}
