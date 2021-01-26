pipeline {
    agent any
    
    stages {
        stage('Assemble') {
            steps {
                echo 'Assembling..'
                sh 'ls -la'
            }
            post {
                always {
                    
                    sh 'pwd'
                    sh 'javac '
                    
                }
            }
        }
    }
}