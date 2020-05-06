cd $HOME
git clone --branch=master https://${GITHUB_TOKEN}@github.com/MrExplode/ltc4j ltc
cd ltc
mvn install
cd $HOME
git clone --branch=master https://${GITHUB_TOKEN}@github.com/hoijui/JavaOSC osc
cd osc
mvn install
