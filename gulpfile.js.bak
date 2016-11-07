var gulp = require('gulp');
var aws = require('aws-sdk');
var sequence = require('run-sequence');
var cloudformation = require('gulp-cloudformation');
var gutil = require('gulp-util');

var aws_opts = {
    region: 'eu-west-1'
};

gulp.task('deploy:elasticache', function(callback) {
    var cloudformation_template = 'src/main/cloudformation/' + gutil.env.env + '-elasticache-things.json';
    aws.config.credentials = new aws.SharedIniFileCredentials({ profile: gutil.env.env });
    return gulp.src([cloudformation_template])
        .pipe(cloudformation.init({   //Only validates the stack files
                region: aws_opts.region,
                accessKeyId: aws.config.credentials.accessKeyId,
                secretAccessKey: aws.config.credentials.secretAccessKey
            })
            .pipe(cloudformation.deploy({
                // Capabilities: [ 'CAPABILITY_IAM' ] //needed if deploying IAM Roles
            }))
            .on('error', function(error) {
                gutil.log('Unable to deploy ElastiCache things exiting With Error', error);
                throw error;
            }));
});

gulp.task('deploy:apigw', function(callback) {
    var cloudformation_template = 'src/main/cloudformation/' + gutil.env.env + '-apigw-things.json';
    aws.config.credentials = new aws.SharedIniFileCredentials({ profile: gutil.env.env });
    // gutil.log('Access key: ' + aws.config.credentials.accessKeyId);
    // gutil.log('Secret key: ' + aws.config.credentials.secretAccessKey);
    return gulp.src([cloudformation_template])
        .pipe(cloudformation.init({   //Only validates the stack files
                region: aws_opts.region,
                accessKeyId: aws.config.credentials.accessKeyId,
                secretAccessKey: aws.config.credentials.secretAccessKey
            })
            .pipe(cloudformation.deploy({
                // Capabilities: [ 'CAPABILITY_IAM' ] //needed if deploying IAM Roles
            }))
            .on('error', function(error) {
                gutil.log('Unable to deploy api-gw exiting With Error', error);
                throw error;
            }));
});

gulp.task('deploy', function(callback) {
    sequence('deploy:elasticache', 'deploy:apigw', callback);
});

gulp.task('default', function(callback) {
    sequence('deploy', callback);
});