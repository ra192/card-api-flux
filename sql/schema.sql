CREATE TABLE merchant
(
    id     bigserial primary key,
    name   character varying(255) NOT NULL UNIQUE,
    secret character varying(255) NOT NULL
);

CREATE TABLE account
(
    id          bigserial primary key,
    active      boolean,
    currency    character varying(255),
    name        character varying(255) NOT NULL UNIQUE,
    merchant_id bigint references merchant(id),
    balance     bigint
);

CREATE TABLE customer
(
    id           bigserial primary key,
    active       boolean                NOT NULL,
    address      character varying(255) NOT NULL,
    address2     character varying(255),
    birth_date   date                   NOT NULL,
    city         character varying(255) NOT NULL,
    country      character varying(255) NOT NULL,
    email        character varying(255),
    first_name   character varying(255) NOT NULL,
    last_name    character varying(255) NOT NULL,
    phone        character varying(255) NOT NULL,
    postal_code  character varying(255) NOT NULL,
    state_region character varying(255) NOT NULL,
    merchant_id  bigint references merchant(id)
);

CREATE TABLE card
(
    id                    bigserial primary key,
    created               timestamp without time zone NOT NULL,
    info                  character varying(255),
    provider_reference_id character varying(255)      NOT NULL,
    type                  character varying(255),
    account_id            bigint references account(id),
    customer_id           bigint references customer(id)
);

CREATE TABLE transaction
(
    id       bigserial primary key,
    order_id character varying(255) NOT NULL,
    status   character varying(255),
    type     character varying(255)
);

CREATE TABLE transaction_item
(
    id              bigserial primary key,
    amount          bigint                      NOT NULL,
    created         timestamp without time zone NOT NULL,
    card_id         bigint references card (id),
    dest_account_id bigint references account(id),
    src_account_id  bigint references account(id),
    transaction_id  bigint references transaction(id)
);

CREATE TABLE transaction_fee
(
    id         bigserial primary key,
    rate       real,
    type       character varying(255),
    account_id bigint references account(id)
);