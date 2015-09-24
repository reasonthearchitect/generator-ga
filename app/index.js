'use strict';
var util = require('util'),
    path = require('path'),
    yeoman = require('yeoman-generator'),
    chalk = require('chalk'),
    _ = require('underscore.string'),
    shelljs = require('shelljs'),
    scriptBase = require('../script-base'),
    packagejs = require(__dirname + '/../package.json'),
    crypto = require("crypto"),
    mkdirp = require('mkdirp'),
    html = require("html-wiring"),
    ejs = require('ejs');

var ScrippsGenerator = module.exports = function ScrippsGenerator(args, options, config) {

    yeoman.generators.Base.apply(this, arguments);

    this.pkg = JSON.parse(html.readFileAsString(path.join(__dirname, '../package.json')));
};

util.inherits(ScrippsGenerator, yeoman.generators.Base);
util.inherits(ScrippsGenerator, scriptBase);

ScrippsGenerator.prototype.askFor = function askFor() {
    var cb = this.async();

    console.log(chalk.red('\n' +
        '              _    __    _       __        ___   ____  _      __        \n' +
        '             | |  / /\\  \\ \\  /  / /\\      | | \\ | |_  \\ \\  / ( (`       \n' +
        '           \\_|_| /_/--\\  \\_\\/  /_/--\\     |_|_/ |_|__  \\_\\/  _)_)       \n'));

    console.log('\nWelcome to the Scripps Generator v' + packagejs.version + '\n');
    var insight = this.insight();
    var questions = 6; // making questions a variable to avoid updating each question by hand when adding additional options

    var prompts = [
        {
            type: 'input',
            name: 'baseName',
            validate: function (input) {
                if (/^([a-zA-Z0-9_]*)$/.test(input)) return true;
                return 'Your application name cannot contain special characters or a blank space, using the default name instead';
            },
            message: '(1/' + questions + ') What is the base name of your application?',
            default: 'scripps'
        },
        {
            type: 'input',
            name: 'packageName',
            validate: function (input) {
                if (/^([a-z_]{1}[a-z0-9_]*(\.[a-z_]{1}[a-z0-9_]*)*)$/.test(input)) return true;
                return 'The package name you have provided is not a valid Java package name.';
            },
            message: '(2/' + questions + ') What is your default Java package name?',
            default: 'com.scripps.myapp'
        },
        {
            type: 'list',
            name: 'sql',
            message: '(3/' + questions + ') Do you want to use an SQL database?',
            choices: [
                {
                    value: 'no',
                    name: 'No'
                },
                {
                    value: 'yes',
                    name: 'Yes'
                }
            ],
            default: 0
        },
        {
            when: function (response) {
                return response.sql == 'yes';
            },
            type: 'list',
            name: 'prodDatabaseType',
            message: '(4/' + questions + ') Which *production* database would you like to use?',
            choices: [
                {
                    value: 'postgresql',
                    name: 'PostgreSQL'
                },
                {
                    value: 'oracle',
                    name: 'Oracle - Warning! The Oracle JDBC driver (ojdbc) is not bundled because it is not Open Source. Please follow our documentation to install it manually.'
                }
            ],
            default: 0
        },
        {
            type: 'list',
            name: 'elastic',
            message: '(5/' + questions + ') Do you want to use elastic in your application?',
            choices: [
                {
                    value: 'no',
                    name: 'No'
                },
                {
                    value: 'yes',
                    name: 'Yes'
                }
            ],
            default: 0
        }
    ];

    this.baseName = this.config.get('baseName');
    this.packageName = this.config.get('packageName');
    this.elastic = this.config.get('elastic');
    this.sql = this.config.get('sql');
    this.mongo = this.config.get('mongo');
    this.prodDatabaseType  = this.config.get('prodDatabaseType');
    this.packageNameGenerated = '';
    var generated = "generated";
    
    
    this.packagejs = packagejs;

    if (this.baseName != null &&
        this.packageName != null &&
        this.sql != null &&
        this.mongo != null &&
        this.elastic != null &&
        this.prodDatabaseType != null
        ) {
            console.log(chalk.green('This is an existing project, using the configuration from your .yo-rc.json file \n' +
            'to re-generate the project...\n'));

        cb();
    } else {
        this.prompt(prompts, function (props) {
            if (props.insight !== undefined) {
                insight.optOut = !props.insight;
            }
            this.baseName = props.baseName;
            this.elastic = props.elastic;
            this.mongo = "no";
            this.sql = props.sql;
            this.packageName = props.packageName;
            this.prodDatabaseType = props.prodDatabaseType;
            var generated = ".generated";
            this.packageNameGenerated = props.packageName +  generated;
            console.log(this.packageNameGenerated);
            if (this.elastic == null) {
                this.elastic = 'no';
            }

            cb();
        }.bind(this));
    }
};

ScrippsGenerator.prototype.app = function app() {
    var insight = this.insight();
    insight.track('generator', 'app');
    insight.track('app/searchEngine', this.searchEngine);

    var packageFolder = this.packageName.replace(/\./g, '/');
    var javaDir = 'src/main/java/' + packageFolder + '/';
    var groovtTest = 'src/test/groovy/' + packageFolder + '/';
    var resourceDir = 'src/main/resources/';
    var conf = "conf/"
    var webappDir = 'src/main/webapp/';
    var interpolateRegex = /<%=([\s\S]+?)%>/g; // so that tags in templates do not get mistreated as _ templates

    // Remove old files

    // Note that both these are due to bugs in the Spring/Liquibase/Swagger solutions,
    this.copy(resourceDir + '/templates/error.html', resourceDir + 'templates/error.html');
    this.copy('src/main/webapp/README.md', 'src/main/webapp/README.md');

    // Application name modified, using each technology's conventions
    this.angularAppName = _.camelize(_.slugify(this.baseName)) + 'App';
    this.camelizedBaseName = _.camelize(this.baseName);
    this.slugifiedBaseName = _.slugify(this.baseName);

    // Create application
    this.template('_README.md', 'README.md', this, {});
    this.copy('gitignore', '.gitignore');
    this.copy('gitattributes', '.gitattributes');
    this.template('_build.gradle', 'build.gradle', this, {});
    this.template('_settings.gradle', 'settings.gradle', this, {});  
    this.template('_gradle.properties', 'gradle.properties', this, {});
    this.template('_sonar.gradle', 'sonar.gradle', this, {});
    this.copy('_Dockerfile', 'Dockerfile');
    // Create the conf folder.
    mkdirp(conf);

    this.template('_profile_dev.gradle', conf + 'profile_dev.gradle', this, {'interpolate': interpolateRegex});
    this.template('_profile_prod.gradle', conf + 'profile_prod.gradle', this, {'interpolate': interpolateRegex});
    this.template('_profile_fast.gradle', conf + 'profile_fast.gradle', this, {'interpolate': interpolateRegex});

    this.template('_mapstruct.gradle', conf + 'mapstruct.gradle', this, {'interpolate': interpolateRegex});
    this.template('_gatling.gradle', conf + 'gatling.gradle', this, {});
    this.template('_liquibase.gradle', conf + 'liquibase.gradle', this, {});
    this.copy('gradlew', 'gradlew');

    this.copy('gradlew.bat', 'gradlew.bat');
    this.copy('gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar');
    this.copy('gradle/wrapper/gradle-wrapper.properties', 'gradle/wrapper/gradle-wrapper.properties');
    

    
    // Create Java resource files
    mkdirp(resourceDir);
    this.copy(resourceDir + '/banner.txt', resourceDir + '/banner.txt');
    // Thymeleaf templates

    this.template(resourceDir + '_logback.xml', resourceDir + 'logback.xml', this, {'interpolate': interpolateRegex});

    this.template(resourceDir + '/config/_application.yml', resourceDir + 'config/application.yml', this, {});
    this.template(resourceDir + '/config/_application-dev.yml', resourceDir + 'config/application-dev.yml', this, {});
    this.template(resourceDir + '/config/_application-prod.yml', resourceDir + 'config/application-prod.yml', this, {});

    // Create Java files
    this.template('src/main/java/package/_Application.java', javaDir + '/Application.java', this, {});
    this.template('src/main/java/package/config/_Constants.java', javaDir + 'generated/config/Constants.java', this, {});
    //this.template('src/main/java/package/_ApplicationWebXml.java', javaDir + '/ApplicationWebXml.java', this, {});
    this.template('src/main/java/package/aop/logging/_LoggingAspect.java', javaDir + 'generated/aop/logging/LoggingAspect.java', this, {});
    this.template('src/main/java/package/config/apidoc/_SwaggerConfiguration.java', javaDir + 'generated/config/apidoc/SwaggerConfiguration.java', this, {});

    this.template('src/main/java/package/async/_ExceptionHandlingAsyncTaskExecutor.java', javaDir + 'generated/async/ExceptionHandlingAsyncTaskExecutor.java', this, {});
    this.template('src/main/java/package/config/_AsyncConfiguration.java', javaDir + 'generated/config/AsyncConfiguration.java', this, {});

    
    this.template('src/main/java/package/config/_JacksonConfiguration.java', javaDir + 'generated/config/JacksonConfiguration.java', this, {});
    this.template('src/main/java/package/config/_LoggingAspectConfiguration.java', javaDir + 'generated/config/LoggingAspectConfiguration.java', this, {});
    this.template('src/main/java/package/config/_MetricsConfiguration.java', javaDir + 'generated/config/MetricsConfiguration.java', this, {});


    // error handler code - server side
    this.template('src/main/java/package/web/rest/errors/_ErrorConstants.java', javaDir + 'generated/web/rest/errors/ErrorConstants.java', this, {});
    this.template('src/main/java/package/web/rest/errors/_CustomParameterizedException.java', javaDir + 'generated/web/rest/errors/CustomParameterizedException.java', this, {});
    this.template('src/main/java/package/web/rest/errors/_ErrorDTO.java', javaDir + 'generated/web/rest/errors/ErrorDTO.java', this, {});
    //this.template('src/main/java/package/web/rest/errors/_ExceptionTranslator.java', javaDir + 'web/rest/errors/ExceptionTranslator.java', this, {});
    this.template('src/main/java/package/web/rest/errors/_FieldErrorDTO.java', javaDir + 'generated/web/rest/errors/FieldErrorDTO.java', this, {});
    this.template('src/main/java/package/web/rest/errors/_ParameterizedErrorDTO.java', javaDir + 'generated/web/rest/errors/ParameterizedErrorDTO.java', this, {});


    this.template('src/main/java/package/web/rest/_LogsRest.java', javaDir + 'generated/web/rest/LogsRest.java', this, {});
    this.template('src/main/java/package/web/rest/dto/_LoggerDto.java', javaDir + 'generated/web/rest/dto/LoggerDto.java', this, {});

    this.template('src/main/java/package/domain/util/_CustomLocalDateSerializer.java', javaDir + 'generated/domain/util/CustomLocalDateSerializer.java', this, {});
    this.template('src/main/java/package/domain/util/_CustomDateTimeSerializer.java', javaDir + 'generated/domain/util/CustomDateTimeSerializer.java', this, {});
    this.template('src/main/java/package/domain/util/_CustomDateTimeDeserializer.java', javaDir + 'generated/domain/util/CustomDateTimeDeserializer.java', this, {});
    this.template('src/main/java/package/domain/util/_ISO8601LocalDateDeserializer.java', javaDir + 'generated/domain/util/ISO8601LocalDateDeserializer.java', this, {});

    this.template('src/main/java/package/web/rest/util/_HeaderUtil.java', javaDir + 'generated/web/rest/util/HeaderUtil.java', this, {});
    this.template('src/main/java/package/web/rest/util/_PaginationUtil.java', javaDir + 'generated/web/rest/util/PaginationUtil.java', this, {});
        

    
    if (this.elastic == 'yes' ) {
        this.template('src/main/java/package/config/_ElasticConfiguration.java', javaDir + 'generated/config/ElasticConfiguration.java', this, {});
    }
    if (this.sql == 'yes') {
        this.template('src/main/java/package/domain/util/_FixedH2Dialect.java', javaDir + 'generated/domain/util/FixedH2Dialect.java', this, {});
        this.template('src/main/java/package/config/_DatabaseConfiguration.java', javaDir + 'generated/config/DatabaseConfiguration.java', this, {});
        this.template('src/main/java/package/security/_SpringSecurityAuditorAware.java', javaDir + 'generated/security/SpringSecurityAuditorAware.java', this, {});
        this.copy(resourceDir + 'h2.server.properties', resourceDir + '.h2.server.properties');
        this.template(resourceDir + '/config/liquibase/changelog/_initial_schema.xml', resourceDir + 'config/liquibase/changelog/00000000000000_initial_schema.xml', this, {'interpolate': interpolateRegex});
        this.copy(resourceDir + '/config/liquibase/master.xml', resourceDir + 'config/liquibase/master.xml');
    }
    if (this.mongo == 'yes') {
        this.template('src/main/java/package/config/_MongoConfiguration.java', javaDir + 'generated/config/MongConfiguration.java', this, {});
        this.copy(resourceDir + '/config/mongeez/master.xml', resourceDir + 'config/mongeez/master.xml');
    }

    //this.template('src/main/java/package/security/_Http401UnauthorizedEntryPoint.java', javaDir + 'security/Http401UnauthorizedEntryPoint.java', this, {});
    
    // Create Test Java files
    var testDir = 'src/test/java/' + packageFolder + '/';
    var testResourceDir = 'src/test/resources/';
    mkdirp(testDir);

    this.template(testResourceDir + 'config/_application.yml', testResourceDir + 'config/application.yml', this, {});
    this.template(testResourceDir + '_logback-test.xml', testResourceDir + 'logback-test.xml', this, {});

    this.template('src/test/java/package/config/jbehave/_AbstractSpringJBehaveStory.java', testDir + 'generated/config/jbehave/AbstractSpringJBehaveStory.java', this, {});
    this.template('src/test/java/package/config/jbehave/_AcceptanceTest.java', testDir + 'generated/config/jbehave/AcceptanceTest.java', this, {});
    this.template('src/test/java/package/config/jbehave/_AcceptanceTestsConfiguration.java', testDir + 'generated/config/jbehave/AcceptanceTestsConfiguration.java', this, {});
    this.template('src/test/java/package/config/jbehave/_Steps.java', testDir + 'generated/config/jbehave/Steps.java', this, {});
/*
    // Create Webapp
    mkdirp(webappDir);
*/
    this.config.set('baseName', this.baseName);
    this.config.set('packageName', this.packageName);
    this.config.set('packageNameGenerated', this.packageNameGenerated);
    this.config.set('packageFolder', packageFolder);
    this.config.set('elastic', this.elastic);
    this.config.set('sql', this.sql);
    this.config.set('mongo', this.mongo);
    this.config.set('prodDatabaseType', this.prodDatabaseType);
};


ScrippsGenerator.prototype._injectDependenciesAndConstants = function _injectDependenciesAndConstants() {
    if (this.options['skip-install']) {
        this.log(
            'After running `npm install & bower install`, inject your front end dependencies' +
            '\ninto your source code by running:' +
            '\n' +
            '\n' + chalk.yellow.bold('grunt wiredep') +
            '\n' +
            '\n ...and generate the Angular constants with:' +
            '\n' + chalk.yellow.bold('grunt ngconstant:dev')
        );
    } 
};
