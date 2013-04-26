rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:grails-plugins/grails-hibernate4-plugin.git
cd grails-hibernate4-plugin
grails clean
grails compile
grails publish-plugin --noScm --snapshot --stacktrace
#grails publish-plugin --noScm --stacktrace
