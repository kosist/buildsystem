#!/usr/bin/env groovy

//note: this script assumes that it will be invoked from another script after that script has defined the necessary parameters

//These are: utfPath, vipbPath, vipbInstall, lvVersion, repoName

//This script further assumes that Jenkins is configured (via the Pipeline Shared Libraries plugin) to implicitly include https://github.com/LabVIEW-DCAF/buildsystem

def call(utfPath,vipbPath,lvVersion,repoName){
def continueBuild
  node{
        echo 'Starting build...'
      stage ('Pre-Clean'){
        preClean()
      }
      stage ('SCM_Checkout'){
        echo 'Attempting to get source from repo...'
        checkout scm
      }
        stage ('Check Preconditions for Build'){
        continueBuild=checkCommits()
      }
    if(continueBuild){
        stage ('Temp Directories'){
          bat 'mkdir build_temp'
        }
        stage ('UTF'){
          utfTest(utfPath)    
        }

        stage ('VIPB_Build'){
          vipbBuild(vipbPath,lvVersion)
        }

        stage ('VIP_Deploy'){
          vipPublish(repoName)
        }
      stage ('SCM commit'){
        commitPackageToGit(vipbPath)
      }
        stage ('Post-Clean'){
          postClean()
        }    
    }
  }
}
