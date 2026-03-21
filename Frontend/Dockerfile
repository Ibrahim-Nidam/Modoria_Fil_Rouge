FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production
RUN mkdir -p /tmp/frontend-dist && \
    if [ -d dist/modoria-frontend/browser ]; then \
      cp -r dist/modoria-frontend/browser/. /tmp/frontend-dist/; \
    else \
      cp -r dist/modoria-frontend/. /tmp/frontend-dist/; \
    fi

FROM nginx:1.27-alpine
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /tmp/frontend-dist/ /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
