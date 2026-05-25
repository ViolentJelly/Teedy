pipeline {
	agent any

	environment {
		// Jenkins credentials ID for Docker Hub
		DOCKER_HUB_CREDENTIALS = 'dockerhub_credentials'
		// Docker Hub repository (user/name)
		DOCKER_IMAGE = 'sakuya666/teedy-app'
		// Use build number as the image tag
		DOCKER_TAG = "${env.BUILD_NUMBER}"
	}

	stages {
		stage('Build') {
			steps {
				checkout scmGit(
					branches: [[name: '*/master']],
					extensions: [],
					userRemoteConfigs: [[url: 'https://github.com/ViolentJelly/Teedy.git']]
				)
				sh 'mvn -B -DskipTests clean package'
			}
		}

		stage('Build Image') {
			steps {
				script {
					// Assume Dockerfile is in repo root
					docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
				}
			}
		}

		stage('Upload Image') {
			steps {
				script {
					docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
						docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
						docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
					}
				}
			}
		}

		stage('Run Container') {
			steps {
				script {
					sh 'docker stop teedy-container-8081 || true'
					sh 'docker rm teedy-container-8081 || true'
					docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
						.run('--name teedy-container-8081 -d -p 8081:8080')
					sh 'docker ps --filter "name=teedy-container"'
				}
			}
		}
	}
}