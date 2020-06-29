pipeline {
    agent{node('master')}
    stages {
        stage('1 stage') {
            steps {
                script {
                    cleanWs()
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        try {
                            sh "echo '${password}' | sudo -S docker stop ozornin_alex_ng"
                            sh "echo '${password}' | sudo -S docker container rm ozornin_alex_ng"
                        } catch (Exception e) {
                            print 'empty'
                        }
                    }
                }
                script {
                    echo 'Update from repository'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'OzorninAlex', url: 'https://github.com/alexozornin/sber_devops.git']]])
                }
            }
        }
        stage ('2 stage'){
            steps{
                script{
                     withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        sh "echo '${password}' | sudo -S docker build ${WORKSPACE}/auto -t ozornin_alex_ng"
                        sh "echo '${password}' | sudo -S docker run -d -p 1970:80 --name ozornin_alex_ng -v /home/adminci/is_mount_dir:/stat ozornin_alex_ng"
                    }
                }
            }
        }
        stage ('3 stage'){
            steps{
                script{
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        
                        sh "echo '${password}' | sudo -S docker exec -t ozornin_alex_ng bash -c 'df -h > /stat/stats.txt'"
                        sh "echo '${password}' | sudo -S docker exec -t ozornin_alex_ng bash -c 'top -n 1 -b >> /stat/stats.txt'"
                    }
                }
            }
        }        
    }    
}
