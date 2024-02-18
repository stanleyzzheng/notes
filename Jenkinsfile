pipeline {
    tools {
        // Define the Maven tool with the specified installation name and home directory
        maven 'Maven 3.8.4'
        jdk 'myJDK'
    }
    agent any
    // options {
    //     timeout(time: 5, unit: 'MINUTES') // Set the timeout duration (1 hour in this example)
    // }
    stages {
        stage('Stop Service') {
            steps {
                script {
                    sh 'sudo systemctl stop backend'
                    echo 'stopping service...'
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    def mvnHome = tool 'Maven 3.8.4'
                    def mvnCommand = "${mvnHome}/bin/mvn"
                    echo 'Building..'
                    sh '''
                echo "doing build stuff. ."
                '''
                    sh "${mvnCommand} clean install -DskipTests=true"
                }
            }
        }
        // stage('Test') {
        //     steps {
        //         echo 'Testing..'
        //         sh '''
        //         echo "doing test stuff . ."
        //         '''
        //     }
        //     post {
        //         always {
        //             junit '**/target/surfire-reports/TEST-*.xml'
        //         }
        //     }
        // }
        stage('Deliver') {
            steps {
                // sh 'echo "Deliver...."'

                // withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                //     sh 'nohup java -jar target/firstspringproject-0.0.1-SNAPSHOT.jar > output.log 2>&1 &'
                // }
                sh 'sudo systemctl start backend'

                sh 'echo "doing delivery stuff.."'
            }
        }
    }
}
