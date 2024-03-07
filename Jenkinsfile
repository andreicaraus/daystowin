pipeline {
    agent any
    tools {
        // Make sure Maven and JDK tool names match those configured in Jenkins
        maven 'Maven' // Name of the Maven tool as configured in Jenkins
    }
    parameters {
      choice choices: ['fastSuiteSimple', 'fastSuiteWithAnnotation', 'fastSuiteWithTestCaseId'], name: 'SUITE_NAME'
    }
    environment {
            // FULL_NAME is concatenated from other variables or set directly
            FULL_NAME = "${env.JOB_NAME}_${env.BUILD_NUMBER}"
    }
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        stage('Clone Repository') {
            steps {
                git credentialsId: 'abc8e51b-85c7-47ab-b8d1-84df52a03f51', url: 'https://github.com/andreicaraus/daystowin.git'
            }
        }
        stage('Run TestNG Tests') {
            steps {
                sh 'mvn clean test -DsuiteName=${SUITE_NAME}.xml'
            }
        }

        stage('Publish to Zephyr') {
            steps {
                script {
                    // Use the FULL_NAME environment variable in your publishTestResults command
                    publishTestResults autoCreateTestCases: true, customTestCycle: [
                        customFields: '',
                        description: 'BuildResults',
                        folderId: '13110221',
                        jiraProjectVersion: '',
                        name: "${env.FULL_NAME}"  // Using the FULL_NAME variable here
                    ], filePath: 'target/surefire-reports/junitreports/*.xml', format: 'JUnit XML Result File', projectKey: 'TES', serverAddress: 'https://innovationdays.atlassian.net'
                }
            }
        }
    }
}
