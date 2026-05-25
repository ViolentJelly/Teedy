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
					def imageRef = "${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
					def latestRef = "${env.DOCKER_IMAGE}:latest"
					sh "docker image inspect ${imageRef} >/dev/null"
					echo "Pushing ${imageRef} to Docker Hub"
					withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_TOKEN')]) {
						withEnv(["IMAGE_REF=${imageRef}", "LATEST_REF=${latestRef}", "DOCKER_CONFIG=${env.WORKSPACE}/.docker-tmp"]) {
							retry(3) {
								sh '''#!/bin/sh
								set -eu
								mkdir -p "$DOCKER_CONFIG"
								printf '{}' > "$DOCKER_CONFIG/config.json"
								echo "$DOCKER_HUB_TOKEN" | docker login --username "$DOCKER_HUB_USERNAME" --password-stdin
								docker push "$IMAGE_REF"
								docker tag "$IMAGE_REF" "$LATEST_REF"
								docker push "$LATEST_REF"
								docker logout || true
								rm -rf "$DOCKER_CONFIG"
								'''
							}
						}
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