# Deploy lambdas

'''
mvn lambduh:deploy-lambda -P thing-search
mvn lambduh:deploy-lambda -P thing
mvn lambduh:deploy-lambda -P geo-search
mvn lambduh:deploy-lambda -P geo-id
'''

# Delete lambda

'''
mvn lambduh:delete-lambda -P thing-search
mvn lambduh:delete-lambda -P thing
mvn lambduh:delete-lambda -P geo-search
'''


# Setup AWS ElastiCache and APIGateway

'''
npm install
gulp --env=platarch --role=arn:aws:iam::810385116814:role/JemRayfieldsLambdaExecutionRole
'''

# Requires Blazegraph database to be running
See http://git.svc.ft.com/projects/WHA/repos/wha-toolbox/browse/environment/README.md


# Install Memcached


brew install memcached
sudo gem install lunchy

## Starting / Stopping memcached

$ mkdir ~/Library/LaunchAgents
$ cp /usr/local/Cellar/memcached/$version/homebrew.mxcl.memcached.plist ~/Library/LaunchAgents/
$ lunchy start memcached
$ lunchy stop memcached


## Testing memcached using Telnet
telnet 127.0.0.1 11211
get greeting
END
set greeting 1 0 11
Hello world
STORED
get greeting
VALUE greeting 1 11
Hello world
END
quit


## Build AWS spy memcache
../