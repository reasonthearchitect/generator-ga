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

var StackGenerator = module.exports = function StackGenerator(args, options, config) {

	yeoman.generators.NamedBase.apply(this, arguments);

	this.baseName = this.config.get('baseName');
    this.packageName = this.config.get('packageName');
    this.packageNameGenerated = this.config.get('packageNameGenerated');
    this.packageFolder = this.config.get('packageFolder');
    this.elastic = this.config.get('elastic');
    this.sql = this.config.get('sql');
}

util.inherits(StackGenerator, yeoman.generators.Base);
util.inherits(StackGenerator, scriptBase);

StackGenerator.prototype.askForFields = function askForFields() {

	var cb = this.async();
	var questions = 3;
	var prompts = [
        {
            type: 'list',
            name: 'business',
            message: '(1/' + questions + ') Do you want to generate the business tier?',
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
                return response.business == 'yes';
            },
            type: 'list',
            name: 'facade',
            message: '(2/' + questions + ') Do you want to generate the facade tier?',
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
                return response.facade == 'yes';
            },
            type: 'list',
            name: 'rest',
            message: '(3/' + questions + ') Do you want to generate the rest tier?',
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

    this.entityClass = _.capitalize(this.name);
    this.entityInstance = _.decapitalize(this.name);

	this.prompt(prompts, function (props) {
          
        this.entityName = props.entityName;
        this.business = props.business;
        this.facade = props.facade;
        this.rest = props.rest;
            
        cb();
    }.bind(this));
};

StackGenerator.prototype.buildStack = function buildStack() {

	var packageFolder = this.packageName.replace(/\./g, '/');
    var javaDir = 'src/main/java/' + packageFolder + '/';
    var groovyTest = 'src/test/groovy/' + packageFolder + '/test/';
    var resourceDir = 'src/main/resources/';

    var interpolateRegex = /<%=([\s\S]+?)%>/g; // so that tags in templates do not get mistreated as _ templates

    if (this.business == 'yes') {
		this.template('src/main/java/package/business/_IBusiness.java', javaDir + 'business/I' +    this.entityClass + 'Business.java', this, {});
        this.template('src/main/java/package/business/impl/_Business.java', javaDir + 'business/impl/' +    this.entityClass + 'Business.java', this, {});
        this.template('src/test/groovy/package/business/_BusinessUnitSpec.groovy', groovyTest + 'business/' +    this.entityClass + 'BusinessUnitSpec.groovy', this, {});
    }

    if (this.facade == 'yes') {
    	this.template('src/main/java/package/facade/_IFacade.java', javaDir + 'facade/I' +    this.entityClass + 'Facade.java', this, {});
        this.template('src/main/java/package/facade/impl/_Facade.java', javaDir + 'facade/impl/' +    this.entityClass + 'Facade.java', this, {});
        this.template('src/test/groovy/package/facade/_FacadeUnitSpec.groovy', groovyTest + 'facade/' +    this.entityClass + 'FacadeUnitSpec.groovy', this, {});
    }

    if (this.rest == 'yes') {
    	this.template('src/main/java/package/web/rest/_Rest.java', javaDir + 'web/rest/' +    this.entityClass + 'Rest.java', this, {});
        this.template('src/test/groovy/package/web/rest/_RestUnitSpec.groovy', groovyTest + 'web/rest/' +    this.entityClass + 'RestUnitSpec.groovy', this, {});
    }
};