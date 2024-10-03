package io.pinnacl.academics.school;

import io.pinnacl.commons.auth.AuthUser;
import io.pinnacl.commons.data.mapper.Mapper;
import io.pinnacl.commons.repository.Repository;
import io.pinnacl.commons.service.DefaultRecordService;
import io.pinnacl.commons.validation.Validator;
import io.pinnacl.academics.school.data.domain.School;
import io.pinnacl.core.organisation.country.CountryService;
import io.pinnacl.core.education.school.data.persistence.SchoolEntity;
import io.vertx.core.Future;

public class SchoolService extends DefaultRecordService<School, SchoolEntity> {

    private final CountryService _countryService;

    private SchoolService(Mapper<School, SchoolEntity> mapper, Repository<SchoolEntity> repository,
                          Validator<School> validator, CountryService countryService) {
        super(School.class, mapper, repository, validator);
        _countryService = countryService;
    }

    public static SchoolService create(Mapper<School, SchoolEntity> mapper,
                                       Repository<SchoolEntity> repository,
                                       Validator<School> validator, CountryService countryService) {
        return new SchoolService(mapper, repository, validator, countryService);
    }

    @Override
    public Future<School> onCreate(AuthUser authUser, School school) {
        return orgWithCountryAndSchool(authUser, school)
                .flatMap(schoolWithCountry -> super.onCreate(authUser, schoolWithCountry));
    }


    @Override
    public Future<School> onUpdate(AuthUser authUser, School school) {
        return orgWithCountryAndSchool(authUser, school)
                .flatMap(schoolWithCountry -> super.onUpdate(authUser, schoolWithCountry));
    }

    private Future<School> orgWithCountryAndSchool(AuthUser authUser, School school) {
        return _countryService.retrieveByName(authUser, school.address().addressCountry().name())
                .map(school::withCountry);
    }

}
