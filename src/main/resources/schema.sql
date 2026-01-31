-- Nettoyage des anciennes tables si elles existent
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS Review CASCADE;
DROP TABLE IF EXISTS reactions CASCADE;
DROP TABLE IF EXISTS Settings CASCADE;
DROP TABLE IF EXISTS otp_verifications CASCADE;
DROP TYPE IF EXISTS product_status CASCADE;
DROP TYPE IF EXISTS trip_type CASCADE;

-- Ajout de l'extension pour les UUIDs si elle n'existe pas
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. ENUMERATIONS (Types de données contrôlés)

CREATE TYPE product_status AS ENUM (
    'Draft',
    'Published',
    'PendingConfirmation',
    'PendingDriverConfirmation',
    'Confirmed',
    'Ongoing',
    'Terminated',
    'Expired',
    'Cancelled'
);

CREATE CAST (varchar AS product_status) WITH INOUT AS IMPLICIT;

CREATE TYPE trip_type AS ENUM (
    'ONE_WAY',
    'ROUND_TRIP'
);

CREATE CAST (varchar AS trip_type) WITH INOUT AS IMPLICIT;

-- 2. TABLE PRINCIPALE : PRODUCTS

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,

    -- Données dénormalisées de l'auteur pour l'affichage rapide
    client_id UUID NOT NULL,
    client_name VARCHAR(255),
    client_phone_number VARCHAR(50),
    profile_image_url TEXT,

    -- Champs communs enrichis
    title VARCHAR(100) NOT NULL,
    status product_status DEFAULT 'Draft',
    trip_type trip_type,
    is_negotiable BOOLEAN DEFAULT false,
    payment_method VARCHAR(100),
    departure_location TEXT,
    dropoff_location TEXT,
    meetup_point TEXT,
    trip_intention VARCHAR(100),
    pricing_method VARCHAR(100),
    start_date TIMESTAMPTZ,
    start_time TIME,
    end_date TIMESTAMPTZ,
    end_time TIME,
    reserved_by_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    
    -- Champs spécifiques à l'Annonce (nullable pour les Plannings)
    cost VARCHAR(255),
    baggage_info VARCHAR(255),

    -- Champs spécifiques au Planning (nullable pour les Annonces)
    payment_option VARCHAR(100),
    regular_amount VARCHAR(200),
    discount_percentage DECIMAL,
    discounted_amount DECIMAL,
    
    -- Champ technique pour le polymorphisme
    product_type VARCHAR(50) NOT NULL -- 'PLANNING' ou 'ANNONCE'
);

-- 3. TABLES SOCIALES (INTERACTIONS)

CREATE TABLE Review (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    subject_type TEXT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE reactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID NOT NULL,
    target_id UUID NOT NULL,
    target_type TEXT NOT NULL,
    type TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (actor_id, target_id, target_type) -- Un utilisateur ne peut avoir qu'une réaction par cible
);

-- 4. TABLE DES PREFERENCES UTILISATEUR

CREATE TABLE Settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL UNIQUE, -- Lien logique vers Auth Service
    theme TEXT,
    language TEXT,
    -- ... autres champs de préférences ...
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

-- 5. TABLE OTP VERIFICATIONS

CREATE TABLE otp_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL,
    otp TEXT NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);