# Travis things
fold_start() {
  echo -e "travis_fold:start:$1\033[33;1m$2\033[0m"
}

fold_end() {
  echo -e "\ntravis_fold:end:$1\r"
}

###################################
# Configuration

PROJECT_NAME="Timecode"
PROJECT_OWNER="MrExplode"
PROJECT_HOME=$HOME/build/$PROJECT_OWNER/$PROJECT_NAME
###################################

###################################
# Beginning of the script
###################################

# Building the project
fold_start doc "Building JavaDoc"
mvn javadoc:javadoc
fold_end doc

# Setting up git
cd $HOME
git config --global user.name "ExplodeBot"
git config --global user.email "sunstorm@outlook.hu"

# Cloning webpage
git clone --branch=master https://${GITHUB_TOKEN}@github.com/MrExplode/MrExplode.github.io website

# removing old JD, copying new one, adding to index, committing and then pushing
cd website
# Tricky but the base solution I started with (https://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/)
# just removes everything FROM INDEX, but we only want to replace the changed in the INDEX
rm -rf projects/$PROJECT_NAME/apidocs
mkdir -p projects/$PROJECT_NAME/apidocs
cp -Rf $PROJECT_HOME/target/site/apidocs projects/$PROJECT_NAME
git add -f .
git commit -m "Latest JavaDoc for $PROJECT_NAME #$TRAVIS_BUILD_NUMBER

Latest JavaDoc on a successful Travis CI build, pushed automatically"
echo -e "\e[93mPushing JavaDoc to webpage..."
git push -fq origin master

exit 0