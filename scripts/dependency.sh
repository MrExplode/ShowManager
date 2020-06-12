cd "$HOME"
git clone --branch=master https://"${GITHUB_TOKEN}"@github.com/MrExplode/ltc4j ltc
cd ltc
mvn clean install
