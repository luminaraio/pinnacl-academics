package io.pinnacl.academics.school;

import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.academics.school.data.persistence.SchoolEntity;
import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.validation.Validator;
import io.vertx.core.Future;
import org.codehaus.plexus.util.StringUtils;

import java.util.UUID;

public class SchoolService extends DefaultRecordService<School, SchoolEntity> {


    private SchoolService(Mapper<School, SchoolEntity> mapper, Repository<SchoolEntity> repository,
                          Validator<School> validator) {
        super(School.class, mapper, repository, validator);

    }

    public static SchoolService create(Mapper<School, SchoolEntity> mapper,
                                       Repository<SchoolEntity> repository,
                                       Validator<School> validator) {
        return new SchoolService(mapper, repository, validator);
    }

    @Override
    public Future<School> onCreate(AuthUser authUser, School domain) {
        logMethodEntry("SchoolService.onCreate");
        if (StringUtils.isBlank(domain.metadata().applicationNumberPrefix())) {
            // TODO: Need to be properly processed
            return super.onCreate(authUser,
                    domain.applicationNumberPrefix(UUID.randomUUID().toString()));
        }
        return super.onCreate(authUser, domain);
    }

}
