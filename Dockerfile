FROM nginx
RUN apt-get update && apt-get install -y procps
WORKDIR /usr/share/nginx/html
COPY web_res/content.html /usr/share/nginx/html
CMD cd /usr/share/nginx/html && sed -e s/Docker/"$AUTHOR"/ content.html > content.html ; nginx -g 'daemon off;'
