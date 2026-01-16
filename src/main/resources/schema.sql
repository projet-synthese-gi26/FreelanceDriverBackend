CREATE TABLE IF NOT EXISTS Review (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ride_id UUID,
    author_id UUID,
    subject_id UUID,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


DROP TABLE IF EXISTS ORGANISATION CASCADE;


DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID,
    name VARCHAR(255),
    description VARCHAR(500),
    is_active BOOLEAN,
    standard_price VARCHAR(255),
    departure_location TEXT, -- changed from TIMESTAMPTZ, should be address or coordinates
    arrival_location TEXT,   -- changed from TIMESTAMPTZ, should be address or coordinates
    start_date TIMESTAMPTZ DEFAULT NOW(),
    start_time TIME,
    end_date TIMESTAMPTZ,
    end_time TIME,
    baggage_info VARCHAR(255),
    is_negotiable BOOLEAN,
    payment_method VARCHAR(100),
    title VARCHAR(100),
    status TEXT CHECK(status in ('DRAFT', 'WAITING_CONFIRMATION', 'PUBLISHED', 'IN_PROGRESS', 'CANCELLED', 'EXPIRED')),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    product_urls TEXT[],
    regular_amount VARCHAR(200),
    discount_percentage DECIMAL,
    discounted_amount DECIMAL,
    metadata TEXT[]
);


CREATE TABLE IF NOT EXISTS Organization (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_actor_id UUID REFERENCES BusinessActor(id),
    logo_id TEXT,
    code TEXT,
    service VARCHAR(255),
    is_individual_business BOOLEAN,
    email VARCHAR(255),
    short_name VARCHAR(100),
    long_name VARCHAR(255),
    description VARCHAR(300),
    logo_uri TEXT,
    website_url TEXT,
    social_network VARCHAR(255),
    business_registration_number NUMERIC,
    tax_number NUMERIC,
    capital_share VARCHAR(200),
    ceo_name TEXT,
    year_founded TIMESTAMP,
    keywords TEXT[],
    number_of_employees INT,
    legal_form TEXT,
    is_active BOOLEAN,
    status TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS BusinessActor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT,
    name TEXT,
    phone_number TEXT,
    email_address TEXT
);

CREATE TABLE IF NOT EXISTS Settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT,
    theme TEXT,
    language TEXT,
    long_ride_enabled BOOLEAN,
    short_ride_enabled BOOLEAN,
    privacy_enable BOOLEAN,
    allow_calls BOOLEAN,
    allow_messages BOOLEAN,
    notify_new_rides BOOLEAN,
    notify_ratings BOOLEAN,
    notify_practical_tips BOOLEAN,
    notify_promotions BOOLEAN,
    notify_policy_updates BOOLEAN,
    notify_peak_hour_recommendations BOOLEAN,
    receive_email BOOLEAN,
    receive_sms BOOLEAN,
    receive_push_notifications BOOLEAN,
    receive_whatsapp BOOLEAN,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS Address (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    addressable_type TEXT,
    type TEXT,
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city TEXT,
    state VARCHAR(100),
    locality TEXT,
    zip_code TEXT,
    postal_code TEXT,
    po_box TEXT,
    is_default BOOLEAN,
    neighborhood TEXT,
    informal_description VARCHAR(300),
    latitude NUMERIC,
    longitude NUMERIC,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS Contact (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contactable_id UUID REFERENCES Organization(id),
    contactable_type TEXT,
    first_name TEXT,
    last_name TEXT,
    title TEXT,
    is_email_verified BOOLEAN,
    is_phone_number_verified BOOLEAN,
    is_favorite BOOLEAN,
    phone_number TEXT,
    secondary_phone_number TEXT,
    fax_number TEXT,
    email TEXT,
    secondary_email TEXT,
    email_verified_at TIMESTAMP,
    phone_verified_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS BusinessActor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT,
    name TEXT,
    phone_number TEXT,
    email_address TEXT
);