#!/bin/sh

# Configuration variables
minio_alias="my-minio"
bucket_name="my-bucket"
minio_endpoint="http://minio:9000"
minio_root_user="integration"
minio_root_password="password"

# Wait for MinIO to start
echo "Waiting for MinIO to start..."
until mc alias set $minio_alias $minio_endpoint $minio_root_user $minio_root_password; do
  echo "Waiting for MinIO server..."
  sleep 5
done

# Create bucket
echo "Creating bucket '$bucket_name'..."
mc mb $minio_alias/$bucket_name
echo "Bucket '$bucket_name' created successfully."
