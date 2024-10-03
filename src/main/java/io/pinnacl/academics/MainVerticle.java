package io.pinnacl.academics;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.pinnacl.commons.config.Config;
import io.pinnacl.commons.config.VerticleConfig;
import io.pinnacl.commons.data.mapper.JsonMapper;
import io.pinnacl.commons.service.DbHealthService;
import io.pinnacl.commons.verticle.AbstractMainVerticle;
import io.pinnacl.commons.verticle.ServiceDetailsVerticle;
import io.pinnacl.core.authnz.Authnz;
import io.pinnacl.core.authnz.KeycloakAuthenticator;
import io.pinnacl.core.authnz.RegistrationValidator;
import io.pinnacl.core.authnz.authentication.AuthValidator;
import io.pinnacl.core.authnz.authentication.AuthenticationService;
import io.pinnacl.core.authnz.authentication.AuthenticationVerticle;
import io.pinnacl.core.authnz.authentication.data.registration.OtpNotificationConfig;
import io.pinnacl.core.education.Educations;
import io.pinnacl.core.education.application.ApplicationService;
import io.pinnacl.core.education.application.ApplicationValidator;
import io.pinnacl.core.education.application.ApplicationVerticle;
import io.pinnacl.core.education.application.data.config.SchoolApplicationEmailConfig;
import io.pinnacl.core.education.application.mapper.ApplicationMapper;
import io.pinnacl.core.education.application.repository.ApplicationRepository;
import io.pinnacl.core.education.school.SchoolService;
import io.pinnacl.core.education.school.SchoolValidator;
import io.pinnacl.core.education.school.SchoolVerticle;
import io.pinnacl.core.education.school.mapper.SchoolMapper;
import io.pinnacl.core.education.school.repository.SchoolRepository;
import io.pinnacl.core.education.school.repository.TermRepository;
import io.pinnacl.core.education.school.repository.TuitionFeeRepository;
import io.pinnacl.core.organisation.OrganisationRepository;
import io.pinnacl.core.organisation.OrganisationService;
import io.pinnacl.core.organisation.OrganisationValidator;
import io.pinnacl.core.organisation.Organisations;
import io.pinnacl.core.organisation.booking.OrganisationBookingEmails;
import io.pinnacl.core.organisation.booking.data.config.OrganisationBookingEmailConfig;
import io.pinnacl.core.organisation.booking.mapper.OrganisationBookingEmailMapper;
import io.pinnacl.core.organisation.booking.repository.OrganisationBookingEmailRepository;
import io.pinnacl.core.organisation.booking.service.OrganisationBookingEmailService;
import io.pinnacl.core.organisation.booking.validator.OrganisationBookingEmailValidator;
import io.pinnacl.core.organisation.category.OrganisationCategoryMapper;
import io.pinnacl.core.organisation.category.OrganisationCategoryRepository;
import io.pinnacl.core.organisation.category.OrganisationCategoryService;
import io.pinnacl.core.organisation.contactusfeedback.ContactUsFeedbackEmails;
import io.pinnacl.core.organisation.contactusfeedback.data.config.ContactUsFeedbackEmailConfig;
import io.pinnacl.core.organisation.contactusfeedback.mapper.ContactUsFeedbackEmailMapper;
import io.pinnacl.core.organisation.contactusfeedback.repository.ContactUsFeedbackEmailRepository;
import io.pinnacl.core.organisation.contactusfeedback.service.ContactUsFeedbackEmailService;
import io.pinnacl.core.organisation.contactusfeedback.validator.ContactUsFeedbackEmailValidator;
import io.pinnacl.core.organisation.country.CountryMapper;
import io.pinnacl.core.organisation.country.CountryRepository;
import io.pinnacl.core.organisation.country.CountryService;
import io.pinnacl.core.organisation.data.domain.config.OrganisationConfig;
import io.pinnacl.core.organisation.depositAccount.*;
import io.pinnacl.core.organisation.depositAccount.data.domain.FlutterWaveConfig;
import io.pinnacl.core.organisation.depositAccount.mapper.AccountBankMapper;
import io.pinnacl.core.organisation.depositAccount.mapper.DepositAccountMapper;
import io.pinnacl.core.organisation.language.LanguageRepository;
import io.pinnacl.core.organisation.language.LanguageService;
import io.pinnacl.core.organisation.language.LanguageValidator;
import io.pinnacl.core.organisation.language.Languages;
import io.pinnacl.core.organisation.language.mapper.LanguageMapper;
import io.pinnacl.core.organisation.mapper.OrganisationMapper;
import io.pinnacl.core.userprofile.UserProfileRepository;
import io.pinnacl.core.userprofile.UserProfileService;
import io.pinnacl.core.userprofile.UserProfileValidator;
import io.pinnacl.core.userprofile.UserProfiles;
import io.pinnacl.core.userprofile.mapper.UserProfileMapper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Luminara - Pinnacl Project.
 *
 * @author Luminara Team
 */
public final class MainVerticle extends AbstractMainVerticle {
    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private static final String KEY_GATEWAY_SERVICE = "gateway";
    private static final String DEPOSIT_ACCOUNT = "depositAccount";
    private static final String SCHOOL = "school";
    private static final String APPLICATION = "schoolApplication";
    private static final String KEY_REFRESH_TOKEN = "refreshTokens";
    private static final String KEY_TOTPS = "totps";
    private static final String KEY_LANGUAGES = "languages";
    private static final String FEATURE_ORGANISATION_BOOKING_EMAIL = "organisationBookingEmail";
    private static final String FEATURE_CONTACT_US_FEEDBACK_EMAIL = "contactUsFeedbackEmail";
    // private static final String FEATURE_SCHOOL_APPLICATION_EMAIL = "schoolApplication";

    @Override
    public Logger logger() {
        return CLASS_LOGGER;
    }

    @Override
    protected Future<Void> deployVerticles(Config config,
                                           Map<String, AsyncMap<String, Object>> sharedMaps,
                                           Mutiny.SessionFactory sessionFactory) {
        return validateRequiredSharedMaps(sharedMaps,
                Set.of(KEY_GATEWAY_SERVICE, KEY_REFRESH_TOKEN, KEY_TOTPS, KEY_LANGUAGES))
                .flatMap(_nullValue -> verticleConfigs(config,
                        Set.of("service", "auth", "organisation", "userprofile", "language",
                                DEPOSIT_ACCOUNT, SCHOOL, APPLICATION,
                                FEATURE_ORGANISATION_BOOKING_EMAIL,
                                FEATURE_CONTACT_US_FEEDBACK_EMAIL)))
                .flatMap(verticleConfigs -> deployVerticles(config.asJsonObject(), verticleConfigs,
                        sharedMaps, sessionFactory));
    }

    private Future<Void> deployVerticles(JsonObject config,
                                         Map<String, VerticleConfig> verticleConfigs,
                                         Map<String, AsyncMap<String, Object>> sharedMaps,
                                         Mutiny.SessionFactory sessionFactory) {

        VerticleConfig authnzVerticleConfig = verticleConfigs.get("auth");
        VerticleConfig organisatioVerticleConfig = verticleConfigs.get("organisation");
        var organisationConfig = JsonMapper.fromJsonObject(organisatioVerticleConfig.config(),
                OrganisationConfig.class);
        var organisationBookingConfig = JsonMapper.fromJsonObject(
                verticleConfigs.get(FEATURE_ORGANISATION_BOOKING_EMAIL).config(),
                OrganisationBookingEmailConfig.class);
        var contactUsFeedbackEmailConfig = JsonMapper.fromJsonObject(
                verticleConfigs.get(FEATURE_CONTACT_US_FEEDBACK_EMAIL).config(),
                ContactUsFeedbackEmailConfig.class);
        var schoolApplicationEmailConfig = JsonMapper.fromJsonObject(
                verticleConfigs.get(APPLICATION).config(), SchoolApplicationEmailConfig.class);
        var flutterWaveConfig = JsonMapper.fromJsonObject(
                verticleConfigs.get(DEPOSIT_ACCOUNT).config(), FlutterWaveConfig.class);

        var countryRepository = CountryRepository.create(sessionFactory);
        var languageRepository = LanguageRepository.create(sessionFactory);
        var organisationRepository = OrganisationRepository.create(sessionFactory);
        var userprofileRepository = UserProfileRepository.create(sessionFactory);
        var organisationCategoryRepository = OrganisationCategoryRepository.create(sessionFactory);
        var depositAccountRepository = DepositAccountRepository.create(sessionFactory);
        var accountBankRepository = AccountBankRepository.create(sessionFactory);
        var organisationBookingEmailRepository =
                OrganisationBookingEmailRepository.create(sessionFactory);
        var contactUsFeedbackEmailRepository =
                ContactUsFeedbackEmailRepository.create(sessionFactory);

        var certify = authnzVerticleConfig.config().getJsonObject("certify", new JsonObject());
        var otpNotificationConfig = JsonMapper.fromJsonObject(
                authnzVerticleConfig.config().getJsonObject("otpNotification"),
                OtpNotificationConfig.class);
        var keycloakAuth = KeycloakAuthenticator.create(vertx, authnzVerticleConfig.config(),
                otpNotificationConfig);
        var phoneNumberUtil = PhoneNumberUtil.getInstance();
        var authValidator = AuthValidator.create(certify);
        var registrationValidator = RegistrationValidator.create(organisationRepository, certify);

        var dbHealthService = DbHealthService.fromSessionFactory(sessionFactory);
        var countryService = CountryService.create(CountryMapper.INSTANCE, countryRepository);
        var languageService = LanguageService.create(LanguageMapper.INSTANCE, languageRepository,
                LanguageValidator.create(), sharedMaps.get(KEY_LANGUAGES));
        var organisationCategoryService = OrganisationCategoryService
                .create(OrganisationCategoryMapper.INSTANCE, organisationCategoryRepository);
        var organisationService = OrganisationService.create(OrganisationMapper.INSTANCE,
                organisationRepository, OrganisationValidator.create(), countryService,
                languageService, organisationCategoryService, organisationConfig);
        var userProfileService =
                UserProfileService.create(vertx, discovery(), UserProfileMapper.INSTANCE,
                        userprofileRepository, UserProfileValidator.create(), keycloakAuth);
        var authenticationService = AuthenticationService.create(discovery(), vertx.eventBus(),
                authnzVerticleConfig.workingDir(), HandlebarsTemplateEngine.create(vertx),
                sharedMaps.get(KEY_GATEWAY_SERVICE), sharedMaps.get(KEY_TOTPS), userProfileService);
        var accountBankService =
                AccountBankService.create(AccountBankMapper.INSTANCE, accountBankRepository);
        var depositAccountService =
                DepositAccountService.create(WebClient.create(vertx), DepositAccountMapper.INSTANCE,
                        depositAccountRepository, DepositAccountValidator.create(),
                        accountBankService, organisationService, flutterWaveConfig);
        var organisationBookingEmailService = OrganisationBookingEmailService.create(vertx,
                discovery(), OrganisationBookingEmailMapper.INSTANCE,
                organisationBookingEmailRepository, OrganisationBookingEmailValidator.create(),
                sharedMaps.get(KEY_GATEWAY_SERVICE), HandlebarsTemplateEngine.create(vertx),
                organisatioVerticleConfig.workingDir(), organisationBookingConfig);
        var contactUsFeedbackEmailService = ContactUsFeedbackEmailService.create(vertx, discovery(),
                ContactUsFeedbackEmailMapper.INSTANCE, contactUsFeedbackEmailRepository,
                ContactUsFeedbackEmailValidator.create(), sharedMaps.get(KEY_GATEWAY_SERVICE),
                HandlebarsTemplateEngine.create(vertx), organisatioVerticleConfig.workingDir(),
                contactUsFeedbackEmailConfig);

        var termRepository = TermRepository.create(sessionFactory);
        var tuitionFeeRepository = TuitionFeeRepository.create(sessionFactory);
        var schoolRepository =
                SchoolRepository.create(sessionFactory, termRepository, tuitionFeeRepository);
        var schoolService = SchoolService.create(SchoolMapper.INSTANCE, schoolRepository,
                SchoolValidator.create(schoolRepository), countryService);
        var applicationRepository = ApplicationRepository.create(sessionFactory);
        var applicationService = ApplicationService.create(vertx, discovery(),
                ApplicationMapper.INSTANCE, applicationRepository, ApplicationValidator.create(),
                HandlebarsTemplateEngine.create(vertx), organisatioVerticleConfig.workingDir(),
                schoolApplicationEmailConfig);


        Supplier<DeploymentOptions> deploymentOptions =
                () -> new DeploymentOptions().setConfig(config);

        AuthenticationVerticle.SupportingServices supportingServices =
                new AuthenticationVerticle.SupportingServices(keycloakAuth, authValidator,
                        registrationValidator, phoneNumberUtil);

        var serviceDetailsVerticle = ServiceDetailsVerticle.create(vertx, discovery(), config,
                verticleConfigs.get("service"), "core", dbHealthService);
        var authVerticle = Authnz.create(discovery(), authnzVerticleConfig, authenticationService,
                sharedMaps.get(KEY_REFRESH_TOKEN), supportingServices, otpNotificationConfig);
        var organisationsVerticle = Organisations.create(discovery(),
                verticleConfigs.get("organisation"), organisationService);
        var userProfilesVerticle = UserProfiles.create(discovery(),
                verticleConfigs.get("userprofile"), userProfileService);
        var languageVerticle =
                Languages.create(discovery(), verticleConfigs.get("language"), languageService);
        var depositAccountVerticle = DepositAccounts.create(discovery(),
                verticleConfigs.get(DEPOSIT_ACCOUNT), depositAccountService);

        var schoolVerticle =
                SchoolVerticle.create(discovery(), verticleConfigs.get("school"), schoolService);
        var applicationVerticle = ApplicationVerticle.create(discovery(),
                verticleConfigs.get(APPLICATION), applicationService);
        var educations = Educations.create(schoolVerticle, applicationVerticle);
        var organisationBookingEmails = OrganisationBookingEmails.create(discovery(),
                verticleConfigs.get(FEATURE_ORGANISATION_BOOKING_EMAIL),
                organisationBookingEmailService);
        var contactUsFeedbackEmails = ContactUsFeedbackEmails.create(discovery(),
                verticleConfigs.get(FEATURE_CONTACT_US_FEEDBACK_EMAIL),
                contactUsFeedbackEmailService);

        return Future
                .all(List.of(vertx.deployVerticle(serviceDetailsVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(authVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(organisationsVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(userProfilesVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(languageVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(depositAccountVerticle, deploymentOptions.get()),
                        vertx.deployVerticle(educations, deploymentOptions.get()),
                        vertx.deployVerticle(organisationBookingEmails, deploymentOptions.get()),
                        vertx.deployVerticle(contactUsFeedbackEmails, deploymentOptions.get())))
                .mapEmpty();
    }
}
