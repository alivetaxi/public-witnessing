FROM node:20 AS builder

RUN mkdir /project
WORKDIR /project

RUN npm install -g @angular/cli@17

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=builder /project/nginx.conf /etc/nginx/templates/nginx.conf.template

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*
COPY --from=builder /project/dist/pbwn-frontend/browser .

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 80
CMD ["nginx","-g","daemon off;"]
