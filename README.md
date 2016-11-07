
# Deploy lambdas

## Make sure you have set your AWS profile properly
```export AWS_DEFAULT_PROFILE=staging```

### Deploy
```gulp --env=staging --lambdarole=FTFlexServices-Deployer --account=528773984231 --region=eu-west-1 --vpcSecurityGroupId=sg-a6a069c1 --subNetOne=subnet-8406d5e0 --subNetTwo=subnet-0ecede57 --subNetGroupId=videotest```


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