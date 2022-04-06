FROM node:14
WORKDIR .
COPY . .
RUN npm install
EXPOSE 8080
CMD ["node","--experimental-modules", "--es-module-specifier-resolution=node","app.js"] 