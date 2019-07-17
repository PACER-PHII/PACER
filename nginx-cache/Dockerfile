FROM nginx:latest

ADD wait-for-it.sh .
RUN mkdir -p /data/nginx/cache
COPY nginx.conf /etc/nginx
