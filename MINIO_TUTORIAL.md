# 🖼️ Guide: Tester l'Upload d'Images de Produits avec MinIO

Ce guide vous explique comment tester la fonctionnalité d'upload d'images pour les produits en utilisant MinIO.

## 📋 Prérequis

1. Docker et Docker Compose installés
2. PostgreSQL en cours d'exécution (port 5432)
3. Base de données `shop_service` créée

## 🚀 Étape 1: Démarrer MinIO

### Option A: Avec Docker Compose (Recommandé)

```bash
# À la racine du projet
docker-compose -f docker-compose.dev.yml --profile minio up -d
```

### Option B: Avec Docker directement

```bash
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v minio-data:/data \
  minio/minio server /data --console-address ":9001"
```

### Vérification

1. Accédez à la console MinIO: **http://localhost:9001**
2. Identifiants:
   - Username: `minioadmin`
   - Password: `minioadmin`

## 🏗️ Étape 2: Appliquer les Migrations

```bash
cd services/shop-service

# Appliquer les migrations
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/application-dev-local.yml
```

## ▶️ Étape 3: Démarrer le Service

```bash
# Mode développement avec profil dev-local
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-local

# OU avec Maven
./mvnw clean install
./mvnw spring-boot:run
```

Le service sera disponible sur: **http://localhost:5020**

## 📚 Étape 4: Accéder à Swagger UI

Ouvrez votre navigateur et allez à:

👉 **http://localhost:5020/swagger-ui.html**

## 🧪 Étape 5: Tester la Création d'un Produit avec Image

### 5.1 Créer un produit (sans image)

Dans Swagger UI, trouvez l'endpoint **POST /api/products** et utilisez ce JSON:

```json
{
  "title": "Organic Bananas",
  "description": "Fresh organic bananas from Ecuador",
  "price": 2.50,
  "currencyId": 1,
  "unitId": 1,
  "categoryId": 1,
  "certificationIds": [1],
  "isFresh": true,
  "isAvailable": true
}
```

**Réponse attendue:** Statut 201 avec l'ID du produit créé (par exemple, `id: 11`)

### 5.2 Uploader une image pour le produit

1. Dans Swagger UI, trouvez **POST /api/products/{id}/image**
2. Cliquez sur "Try it out"
3. Renseignez:
   - `id`: L'ID du produit créé (ex: 11)
   - `image`: Cliquez sur "Choose File" et sélectionnez une image (JPEG, PNG, etc.)
4. Cliquez sur "Execute"

**Réponse attendue:** Le produit avec les champs image remplis:

```json
{
  "id": 11,
  "title": "Organic Bananas",
  "imageUrl": "http://localhost:9000/product-images/products/abc123...jpg",
  "imageKey": "products/abc123...jpg",
  ...
}
```

### 5.3 Vérifier l'image dans MinIO

1. Allez sur **http://localhost:9001**
2. Connectez-vous (minioadmin/minioadmin)
3. Cliquez sur le bucket `product-images`
4. Naviguez vers `products/`
5. Vous devriez voir votre image uploadée !

## 🔍 Étape 6: Tester Tous les Endpoints

### 6.1 Récupérer tous les produits

**GET /api/products**

Devrait retourner tous les produits avec leurs URLs d'images.

### 6.2 Récupérer un produit spécifique

**GET /api/products/{id}**

Exemple: `GET /api/products/11`

### 6.3 Rechercher des produits

**GET /api/products/search?q=banana**

### 6.4 Filtrer par catégorie

**GET /api/products/category/1**

### 6.5 Produits disponibles uniquement

**GET /api/products/available**

### 6.6 Produits frais uniquement

**GET /api/products/fresh**

### 6.7 Mettre à jour un produit

**PUT /api/products/11**

```json
{
  "title": "Organic Bananas - Updated",
  "description": "Premium organic bananas from Ecuador",
  "price": 2.99,
  "currencyId": 1,
  "unitId": 1,
  "categoryId": 1,
  "isFresh": true,
  "isAvailable": true
}
```

### 6.8 Soft Delete

**DELETE /api/products/11**

Le produit est marqué comme supprimé mais reste en base.

### 6.9 Restaurer un produit

**PATCH /api/products/11/restore**

### 6.10 Hard Delete (suppression définitive)

**DELETE /api/products/11/hard**

⚠️ **Attention:** Ceci supprime définitivement le produit ET son image de MinIO !

## 🧪 Tests avec cURL

### Créer un produit

```bash
curl -X POST http://localhost:5020/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fresh Strawberries",
    "description": "Organic strawberries, 250g pack",
    "price": 4.99,
    "currencyId": 2,
    "unitId": 2,
    "categoryId": 1,
    "certificationIds": [1],
    "isFresh": true,
    "isAvailable": true
  }'
```

### Uploader une image

```bash
curl -X POST http://localhost:5020/api/products/12/image \
  -H "Content-Type: multipart/form-data" \
  -F "image=@/path/to/your/strawberry.jpg"
```

### Récupérer le produit avec l'image

```bash
curl http://localhost:5020/api/products/12
```

## 📊 Vérification dans la Base de Données

```sql
-- Voir tous les produits avec leurs images
SELECT 
    id, 
    title, 
    price, 
    image_url, 
    image_key,
    is_fresh,
    is_available,
    created_at
FROM product
WHERE is_deleted = FALSE
ORDER BY created_at DESC;

-- Voir les certifications d'un produit
SELECT 
    p.title,
    pc.label AS certification
FROM product p
JOIN product_certification_link pcl ON p.id = pcl.product_id
JOIN product_certification pc ON pcl.certification_id = pc.id
WHERE p.id = 11;
```

## 🐛 Troubleshooting

### Problème: "Bucket not found"

Le bucket est créé automatiquement au démarrage du service. Si vous avez une erreur:

1. Allez sur http://localhost:9001
2. Créez manuellement le bucket `product-images`
3. Redémarrez le service

### Problème: "Connection refused" vers MinIO

Vérifiez que MinIO est bien démarré:

```bash
docker ps | grep minio
```

Si absent, redémarrez MinIO:

```bash
docker-compose -f docker-compose.dev.yml --profile minio up -d
```

### Problème: L'image n'est pas accessible

Les URLs MinIO en local sont de la forme:
`http://localhost:9000/product-images/products/xxx.jpg`

Assurez-vous que:
1. MinIO tourne sur le port 9000
2. Le bucket `product-images` existe
3. Les permissions du bucket permettent la lecture

### Problème: "Only image files are allowed"

Le service n'accepte que les fichiers avec un Content-Type `image/*`:
- JPEG (image/jpeg)
- PNG (image/png)
- GIF (image/gif)
- WebP (image/webp)

## 🎯 Prochaines Étapes

1. ✅ Créer des thumbnails automatiques
2. ✅ Ajouter la compression d'images
3. ✅ Implémenter un système de cache CDN
4. ✅ Ajouter la validation de taille d'image (max 5MB)
5. ✅ Support multi-images par produit

## 📝 Notes Importantes

- Les images sont stockées avec un UUID unique pour éviter les collisions
- Le hard delete supprime automatiquement l'image de MinIO
- Les images restent même si vous supprimez (soft delete) un produit
- Pour la production, remplacez MinIO par AWS S3 (même code, juste changer la config!)

---

**Besoin d'aide ?** Consultez les logs du service pour plus de détails sur les erreurs.
