CREATE TABLE IF NOT EXISTS public.franchise
(
    franchise_id BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS public.branch
(
    branch_id    BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    franchise_id BIGINT       NOT NULL,
    CONSTRAINT fk_branch_franchise
    FOREIGN KEY (franchise_id)
    REFERENCES public.franchise (franchise_id)
    );

CREATE TABLE IF NOT EXISTS public.products
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    stock     INTEGER      NOT NULL,
    branch_id BIGINT       NOT NULL,
    CONSTRAINT fk_products_branch
    FOREIGN KEY (branch_id)
    REFERENCES public.branch (branch_id)
    );

CREATE INDEX IF NOT EXISTS idx_branch_franchise ON branch(franchise_id);
CREATE INDEX IF NOT EXISTS idx_product_branch_stock ON products(branch_id, stock DESC, id);


-- Insertar franquicias
INSERT INTO franchise (name) VALUES ('Franquicia A');
INSERT INTO franchise (name) VALUES ('Franquicia B');

-- Insertar sucursales
INSERT INTO branch (name, franchise_id) VALUES ('Sucursal 1A', 1);
INSERT INTO branch (name, franchise_id) VALUES ('Sucursal 2A', 1);
INSERT INTO branch (name, franchise_id) VALUES ('Sucursal 1B', 2);

-- Insertar productos para franquicia A
INSERT INTO products (name, stock, branch_id) VALUES ('Coca-Cola', 100, 1);
INSERT INTO products (name, stock, branch_id) VALUES ('Pepsi', 80, 1);
INSERT INTO products (name, stock, branch_id) VALUES ('Sprite', 50, 1);

INSERT INTO products (name, stock, branch_id) VALUES ('7UP', 200, 2);
INSERT INTO products (name, stock, branch_id) VALUES ('Fanta', 150, 2);
INSERT INTO products (name, stock, branch_id) VALUES ('Manzana', 120, 2);

-- Insertar productos para franquicia B
INSERT INTO products (name, stock, branch_id) VALUES ('RedBull', 30, 3);
INSERT INTO products (name, stock, branch_id) VALUES ('Gatorade', 60, 3);
INSERT INTO products (name, stock, branch_id) VALUES ('Monster', 90, 3);