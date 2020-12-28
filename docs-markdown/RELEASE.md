## Latest version [ ![Download](https://api.bintray.com/packages/sourcepoint/sourcepoint/cmplibrary/images/download.svg) ](https://bintray.com/sourcepoint/sourcepoint/cmplibrary/_latestVersion)

## Release process
## Steps

<details>
  <summary>1. checkout the `develop` branch</summary>
  
```
    git checkout develop
```
</details>
<details>
  <summary>2. update and merge any remote changes of the current branch you're on</summary>
  
```
    git pull develop
```
</details>
<details>
  <summary>3. update the def VERSION_NAME variable in the build.gradle file</summary>
  
```
    def VERSION_NAME = "X.Y.Z"
```
</details>
<details>
  <summary>4. add the release note into the release_note.txt file located in the root of the project</summary>
  
```
    - first feature
    - second feature
```
</details>
<details>
  <summary>5. add, commit and push your changes</summary>
  
```
    git add .
    git commit 
    git push
```
</details>
<details>
  <summary>6. create a branch called `release/X.Y.Z` and create a pr so that your team members can see what you're preparing to release</summary>
  
```
    git checkout -b release/X.Y.Z
    git push --set-upstream origin release/X.Y.Z
```
</details>
<details>
  <summary>7. the release will be created after the pr is merged into master</summary>
</details>

## Branch naming convention
Branch naming convention is `release/x-x-x`. The `x[major release]-x[release]-x[hot-fix]` signs represent the release tag. e.g. 

If the current tag in your repository is `0.1.4` then your next tag will be `0.2.0` for release branch. The one in the middle 
gets bumped up by `1` and the last one gets reset to `0` so our new release branch name should be `release/0.2.0`. 
Next one will be `release/0.3.0`.

## Files involved for this release process
- `sdk-release.yml`: workflow file for creating a release 
- `cmplibrary/release_note.txt`: it hosts the release notes
- `cmplibrary/version.txt`: it hosts the release version used to name the tag. This file is created automatically from 
Gradle using the `versionTxt` task.
