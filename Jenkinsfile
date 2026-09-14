pipeline {
  // markhobson/maven-chrome bundles Maven, JDK and a headless-capable Chrome +
  // chromedriver, so the same 'mvn verify' that runs unit/integration tests can
  // also run the Selenium system tests inside this one container.
  agent {
    docker { image 'markhobson/maven-chrome:jdk-17' }
  }
  stages {
    stage('Build and test') {
      steps {
        // verify = unit tests + integration tests + headless Selenium system tests
        // (the app is started and stopped automatically around the system tests,
        // see pom.xml). No browser window is ever opened.
        sh 'mvn -B clean verify'
      }
    }
    stage('Coverage report') {
      steps {
        sh 'mvn -B jacoco:report'
      }
    }
  }
  post {
    always {
      junit testResults: 'target/surefire-reports/*.xml,target/failsafe-reports/*.xml', allowEmptyResults: true
      archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
      archiveArtifacts artifacts: 'target/selenium-screenshots/**', allowEmptyArchive: true
    }
  }
}
