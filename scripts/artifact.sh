###################################
# Configuration

PROJECT_NAME="Timecode"
PROJECT_OWNER="MrExplode"
PROJECT_HOME=$HOME/build/$PROJECT_OWNER/$PROJECT_NAME
###################################

###################################
# Beginning of the script
###################################

exit 0
# everything below doesn't work
# Building the project
fold_start install "Building artifact"
mvn install propertyexporter:export
fold_end install

# Setting up git
cd "$HOME"
git config --global user.name "ExplodeBot"
git config --global user.email "sunstorm@outlook.hu"

# Cloning webpage
git clone --branch=master https://"${GITHUB_TOKEN}"@github.com/MrExplode/MrExplode.github.io website

# removing old build, copying new one then commit and push
cd website
mkdir -p projects/$PROJECT_NAME
rm projects/$PROJECT_NAME/*.jar
cp "$PROJECT_HOME"/target/*.jar projects/$PROJECT_NAME
javac "$PROJECT_HOME"/scripts/ProjectListManager.java
java -cp "$PROJECT_HOME"/scripts ProjectListManager "$PROJECT_HOME"/target/info.txt projects/project-list.yml
git add -f .
git commit -m "Latest Artifact for $PROJECT_NAME #$TRAVIS_BUILD_NUMBER

Latest Artifact on a successful Travis CI build, pushed automatically"
echo -e "\e[93mPushing Artifact to webpage..."
git push -fq origin master

#cleaning up
rm -rf "$HOME"/website

exit 0