-- ======================================================
-- 1. IDENTIFICACIONES (ESTÁNDARES INTERNACIONALES)
-- ======================================================

INSERT OR IGNORE INTO identifications (code, name) VALUES
('C.C', 'CÉDULA DE CIUDADANIA'),
('C.E', 'CÉDULA DE EXTRANJERÍA'),
('PAS', 'PASAPORTE'),
('D.N.I', 'DOCUMENTO NACIONAL DE IDENTIDAD'),
('I.N.E', 'CREDENCIAL DE VOTAR INE'),
('S.S.N', 'SOCIAL SECURITY NUMBER'),
('L.C', 'LICENCIA DE CONDUCIR INTERNACIONAL'),
('N.I.E', 'NÚMERO DE IDENTIDAD DE EXTRANJERO')//

-- ======================================================
-- 2. MÉTODOS DE PAGO GLOBALIZADOS
-- ======================================================

INSERT OR IGNORE INTO payment_method (name) VALUES
('DÉBITO'),
('CRÉDITO'),
('EFECTIVO'),
('PAYPAL'),
('APPLE PAY'),
('GOOGLE PAY'),
('TRANSFERENCIA BANCARIA'),
('BILLETERA DIGITAL')//

-- ======================================================
-- 3. PAÍSES (Generación de IDs del 1 al 78)
-- ======================================================

INSERT OR IGNORE INTO countries (id, country, postal_number) VALUES
(1, 'Colombia', '57'), (2, 'Argentina', '54'), (3, 'Bolivia', '591'), (4, 'Brasil', '55'), (5, 'Chile', '56'), 
(6, 'Ecuador', '593'), (7, 'Guyana', '592'), (8, 'Paraguay', '595'), (9, 'Perú', '51'), (10, 'Surinam', '597'), 
(11, 'Uruguay', '598'), (12, 'Venezuela', '58'), (13, 'Costa Rica', '506'), (14, 'Cuba', '53'), (15, 'El Salvador', '503'), 
(16, 'Guatemala', '502'), (17, 'Honduras', '504'), (18, 'Nicaragua', '505'), (19, 'Panamá', '507'), (20, 'República Dominicana', '1'),
(21, 'Estados Unidos', '1'), (22, 'Canadá', '1'), (23, 'México', '52'),
(24, 'Alemania', '49'), (25, 'Austria', '43'), (26, 'Bélgica', '32'), (27, 'Bulgaria', '359'), (28, 'Croacia', '385'), 
(29, 'Dinamarca', '45'), (30, 'Eslovaquia', '421'), (31, 'Eslovenia', '386'), (32, 'España', '34'), (33, 'Estonia', '372'), 
(34, 'Finlandia', '358'), (35, 'Francia', '33'), (36, 'Grecia', '30'), (37, 'Hungría', '36'), (38, 'Irlanda', '353'), 
(39, 'Islandia', '354'), (40, 'Italia', '39'), (41, 'Letonia', '371'), (42, 'Lituania', '370'), (43, 'Luxemburgo', '352'), 
(44, 'Malta', '356'), (45, 'Noruega', '47'), (46, 'Países Bajos', '31'), (47, 'Polonia', '48'), (48, 'Portugal', '351'), 
(49, 'Reino Unido', '44'), (50, 'República Checa', '420'), (51, 'Rumania', '40'), (53, 'Suecia', '46'), 
(54, 'Suiza', '41'), (55, 'Ucrania', '380'),
(56, 'Arabia Saudita', '966'), (57, 'China', '86'), (58, 'Corea del Sur', '82'), (59, 'Emiratos Árabes Unidos', '971'), 
(60, 'Filipinas', '63'), (61, 'India', '91'), (62, 'Indonesia', '62'), (63, 'Israel', '972'), (64, 'Japón', '81'), 
(65, 'Malasia', '60'), (66, 'Qatar', '974'), (67, 'Singapur', '65'), (68, 'Tailandia', '66'), (69, 'Taiwán', '886'), 
(70, 'Turquía', '90'), (71, 'Vietnam', '84'),
(72, 'Australia', '61'), (73, 'Egipto', '20'), (74, 'Kenia', '254'), (75, 'Marruecos', '212'), (76, 'Nigeria', '234'), 
(77, 'Nueva Zelanda', '64'), (78, 'Sudáfrica', '27')//

-- ======================================================
-- 4. UBICACIONES MUNDIALES CON CLAVE FORÁNEA
-- ======================================================

-- COLOMBIA (ID 1)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(1, 'Antioquia', 'Medellín'), (1, 'Antioquia', 'Bello'), (1, 'Antioquia', 'Itagüí'), (1, 'Antioquia', 'Envigado'), (1, 'Antioquia', 'Rionegro'), (1, 'Antioquia', 'Apartadó'),
(1, 'Atlántico', 'Barranquilla'), (1, 'Atlántico', 'Soledad'), (1, 'Atlántico', 'Puerto Colombia'), (1, 'Atlántico', 'Malambo'), (1, 'Atlántico', 'Sabanagrande'),
(1, 'Bogotá', 'Bogotá D.C.'), (1, 'Cundinamarca', 'Soacha'), (1, 'Cundinamarca', 'Chía'), (1, 'Cundinamarca', 'Zipaquirá'), (1, 'Cundinamarca', 'Facatativá'), (1, 'Cundinamarca', 'Girardot'),
(1, 'Valle del Cauca', 'Cali'), (1, 'Valle del Cauca', 'Buenaventura'), (1, 'Valle del Cauca', 'Palmira'), (1, 'Valle del Cauca', 'Tuluá'), (1, 'Valle del Cauca', 'Buga'), (1, 'Valle del Cauca', 'Cartago'),
(1, 'Bolívar', 'Cartagena'), (1, 'Bolívar', 'Magangué'), (1, 'Bolívar', 'Turbaco'), (1, 'Magdalena', 'Santa Marta'), (1, 'Magdalena', 'Ciénaga'),
(1, 'Santander', 'Bucaramanga'), (1, 'Santander', 'Floridablanca'), (1, 'Santander', 'Barrancabermeja'), (1, 'Santander', 'Girón'), (1, 'Santander', 'Piedecuesta')//

-- ARGENTINA (ID 2)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(2, 'Buenos Aires', 'CABA'), (2, 'Buenos Aires', 'La Plata'), (2, 'Buenos Aires', 'Mar del Plata'), (2, 'Buenos Aires', 'Bahía Blanca'), (2, 'Buenos Aires', 'Quilmes'), (2, 'Buenos Aires', 'Tandil'),
(2, 'Córdoba', 'Córdoba'), (2, 'Córdoba', 'Río Cuarto'), (2, 'Córdoba', 'Villa Carlos Paz'), (2, 'Santa Fe', 'Rosario'), (2, 'Santa Fe', 'Santa Fe de la Vera Cruz'), (2, 'Santa Fe', 'Rafaela'),
(2, 'Mendoza', 'Mendoza'), (2, 'Mendoza', 'San Rafael'), (2, 'Mendoza', 'Godoy Cruz'), (2, 'Tucumán', 'San Miguel de Tucumán'), (2, 'Salta', 'Salta'), (2, 'Neuquén', 'Neuquén')//

-- BOLIVIA (ID 3)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(3, 'La Paz', 'La Paz'), (3, 'La Paz', 'El Alto'), (3, 'Santa Cruz', 'Santa Cruz de la Sierra'), (3, 'Cochabamba', 'Cochabamba')//

-- BRASIL (ID 4)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(4, 'São Paulo', 'São Paulo'), (4, 'São Paulo', 'Campinas'), (4, 'São Paulo', 'Guarulhos'), (4, 'São Paulo', 'Santos'), (4, 'São Paulo', 'Ribeirão Preto'), (4, 'São Paulo', 'São Bernardo do Campo'),
(4, 'Rio de Janeiro', 'Rio de Janeiro'), (4, 'Rio de Janeiro', 'Niterói'), (4, 'Rio de Janeiro', 'Duque de Caxias'), (4, 'Rio de Janeiro', 'Búzios'), (4, 'Rio de Janeiro', 'Petrópolis'),
(4, 'Minas Gerais', 'Belo Horizonte'), (4, 'Minas Gerais', 'Uberlândia'), (4, 'Minas Gerais', 'Ouro Preto'), (4, 'Bahia', 'Salvador'), (4, 'Bahia', 'Feira de Santana'),
(4, 'Paraná', 'Curitiba'), (4, 'Paraná', 'Foz do Iguaçu'), (4, 'Paraná', 'Londrina'), (4, 'Rio Grande do Sul', 'Porto Alegre'), (4, 'Rio Grande do Sul', 'Caxias do Sul')//

-- CHILE (ID 5)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(5, 'Región Metropolitana', 'Santiago'), (5, 'Región Metropolitana', 'Puente Alto'), (5, 'Región Metropolitana', 'Maipú'),
(5, 'Valparaíso', 'Valparaíso'), (5, 'Valparaíso', 'Viña del Mar'), (5, 'Valparaíso', 'San Antonio'), (5, 'Biobío', 'Concepción'), (5, 'Biobío', 'Talcahuano'), (5, 'Biobío', 'Los Ángeles'),
(5, 'Antofagasta', 'Antofagasta'), (5, 'Antofagasta', 'Calama'), (5, 'Coquimbo', 'La Serena'), (5, 'Coquimbo', 'Coquimbo'), (5, 'Araucanía', 'Temuco')//

-- ECUADOR (ID 6)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(6, 'Pichincha', 'Quito'), (6, 'Guayas', 'Guayaquil'), (6, 'Azuay', 'Cuenca'), (6, 'Manabí', 'Manta'), (6, 'Manabí', 'Portoviejo')//

-- PARAGUAY (ID 8)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(8, 'Asunción', 'Asunción'), (8, 'Central', 'San Lorenzo'), (8, 'Alto Paraná', 'Ciudad del Este')//

-- PERÚ (ID 9)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(9, 'Lima', 'Lima'), (9, 'Lima', 'Callao'), (9, 'Lima', 'Miraflores'), (9, 'Lima', 'San Isidro'), (9, 'Arequipa', 'Arequipa'), (9, 'Cusco', 'Cusco'), (9, 'Cusco', 'Urubamba'),
(9, 'La Libertad', 'Trujillo'), (9, 'Piura', 'Piura'), (9, 'Lambayeque', 'Chiclayo'), (9, 'Ica', 'Ica'), (9, 'Ica', 'Paracas')//

-- URUGUAY (ID 11)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(11, 'Montevideo', 'Montevideo'), (11, 'Canelones', 'Ciudad de la Costa'), (11, 'Maldonado', 'Punta del Este')//

-- VENEZUELA (ID 12)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(12, 'Distrito Capital', 'Caracas'), (12, 'Zulia', 'Maracaibo'), (12, 'Carabobo', 'Valencia'), (12, 'Lara', 'Barquisimeto')//

-- COSTA RICA (ID 13)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(13, 'San José', 'San José'), (13, 'Alajuela', 'Alajuela'), (13, 'Puntarenas', 'Puntarenas')//

-- PANAMÁ (ID 19)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(19, 'Panamá', 'Ciudad de Panamá'), (19, 'Panamá', 'San Miguelito'), (19, 'Colón', 'Ciudad de Colón')//

-- REP. DOMINICANA (ID 20)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(20, 'Santo Domingo', 'Santo Domingo'), (20, 'Santiago', 'Santiago de los Caballeros'), (20, 'La Altagracia', 'Punta Cana')//

-- ESTADOS UNIDOS (ID 21)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(21, 'California', 'Los Angeles'), (21, 'California', 'San Francisco'), (21, 'California', 'San Diego'), (21, 'California', 'San Jose'), (21, 'California', 'Sacramento'), (21, 'California', 'Fresno'), (21, 'California', 'Long Beach'), (21, 'California', 'Oakland'),
(21, 'New York', 'New York City'), (21, 'New York', 'Buffalo'), (21, 'New York', 'Rochester'), (21, 'New York', 'Yonkers'), (21, 'New York', 'Syracuse'), (21, 'New York', 'Albany'),
(21, 'Florida', 'Miami'), (21, 'Florida', 'Orlando'), (21, 'Florida', 'Tampa'), (21, 'Florida', 'Jacksonville'), (21, 'Florida', 'St. Petersburg'), (21, 'Florida', 'Fort Lauderdale'),
(21, 'Texas', 'Houston'), (21, 'Texas', 'San Antonio'), (21, 'Texas', 'Dallas'), (21, 'Texas', 'Austin'), (21, 'Texas', 'Fort Worth'), (21, 'Texas', 'El Paso'),
(21, 'Illinois', 'Chicago'), (21, 'Illinois', 'Aurora'), (21, 'Illinois', 'Naperville'), (21, 'Illinois', 'Springfield'),
(21, 'Pennsylvania', 'Philadelphia'), (21, 'Pennsylvania', 'Pittsburgh'), (21, 'Pennsylvania', 'Allentown'),
(21, 'Ohio', 'Columbus'), (21, 'Ohio', 'Cleveland'), (21, 'Ohio', 'Cincinnati'),
(21, 'Georgia', 'Atlanta'), (21, 'Georgia', 'Augusta'), (21, 'Georgia', 'Savannah'),
(21, 'Washington', 'Seattle'), (21, 'Washington', 'Spokane'), (21, 'Washington', 'Tacoma'),
(21, 'Massachusetts', 'Boston'), (21, 'Massachusetts', 'Worcester'), (21, 'Massachusetts', 'Cambridge'),
(21, 'Nevada', 'Las Vegas'), (21, 'Nevada', 'Henderson'), (21, 'Nevada', 'Reno')//

-- CANADÁ (ID 22)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(22, 'Ontario', 'Toronto'), (22, 'Ontario', 'Ottawa'), (22, 'Ontario', 'Mississauga'), (22, 'Ontario', 'Brampton'), (22, 'Ontario', 'Hamilton'), (22, 'Ontario', 'London'),
(22, 'Quebec', 'Montreal'), (22, 'Quebec', 'Quebec City'), (22, 'Quebec', 'Laval'),
(22, 'British Columbia', 'Vancouver'), (22, 'British Columbia', 'Surrey'), (22, 'British Columbia', 'Burnaby'), (22, 'British Columbia', 'Richmond'), (22, 'British Columbia', 'Victoria'),
(22, 'Alberta', 'Calgary'), (22, 'Alberta', 'Edmonton'), (22, 'Manitoba', 'Winnipeg'), (22, 'Nova Scotia', 'Halifax')//

-- MÉXICO (ID 23)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(23, 'Ciudad de México', 'CDMX'), (23, 'Ciudad de México', 'Coyoacán'), (23, 'Ciudad de México', 'Polanco'),
(23, 'Jalisco', 'Guadalajara'), (23, 'Jalisco', 'Zapopan'), (23, 'Jalisco', 'Puerto Vallarta'), (23, 'Jalisco', 'Tlaquepaque'),
(23, 'Nuevo León', 'Monterrey'), (23, 'Nuevo León', 'San Pedro Garza García'), (23, 'Nuevo León', 'Guadalupe'),
(23, 'Quintana Roo', 'Cancún'), (23, 'Quintana Roo', 'Playa del Carmen'), (23, 'Quintana Roo', 'Tulum'), (23, 'Quintana Roo', 'Cozumel'),
(23, 'Baja California', 'Tijuana'), (23, 'Baja California', 'Mexicali'), (23, 'Baja California', 'Ensenada'), (23, 'Baja California Sur', 'Cabo San Lucas'),
(23, 'Yucatán', 'Mérida'), (23, 'Yucatán', 'Valladolid'), (23, 'Guanajuato', 'León'), (23, 'Guanajuato', 'San Miguel de Allende'),
(23, 'Puebla', 'Puebla de Zaragoza'), (23, 'Puebla', 'Cholula'), (23, 'Querétaro', 'Santiago de Querétaro'), (23, 'Chihuahua', 'Ciudad Juárez')//

-- ALEMANIA (ID 24)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(24, 'Nordrhein-Westfalen', 'Köln'), (24, 'Nordrhein-Westfalen', 'Düsseldorf'), (24, 'Nordrhein-Westfalen', 'Dortmund'), (24, 'Nordrhein-Westfalen', 'Essen'),
(24, 'Baviera', 'München'), (24, 'Baviera', 'Nürnberg'), (24, 'Baviera', 'Augsburg'), (24, 'Baviera', 'Regensburg'),
(24, 'Berlín', 'Berlín'), (24, 'Hamburgo', 'Hamburgo'), (24, 'Bremen', 'Bremen'), (24, 'Hessen', 'Frankfurt am Main'), (24, 'Hessen', 'Wiesbaden'),
(24, 'Baden-Württemberg', 'Stuttgart'), (24, 'Baden-Württemberg', 'Mannheim'), (24, 'Baden-Württemberg', 'Karlsruhe'), (24, 'Sajonia', 'Leipzig'), (24, 'Sajonia', 'Dresden')//

-- AUSTRIA (ID 25)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(25, 'Wien', 'Wien'), (25, 'Steiermark', 'Graz'), (25, 'Oberösterreich', 'Linz'), (25, 'Salzburg', 'Salzburg'), (25, 'Tirol', 'Innsbruck')//

-- DINAMARCA (ID 29)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(29, 'Region Hovedstaden', 'København'), (29, 'Region Midtjylland', 'Aarhus')//

-- ESPAÑA (ID 32)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(32, 'Madrid', 'Madrid'), (32, 'Madrid', 'Móstoles'), (32, 'Madrid', 'Alcalá de Henares'), (32, 'Madrid', 'Fuenlabrada'), (32, 'Madrid', 'Leganés'),
(32, 'Cataluña', 'Barcelona'), (32, 'Cataluña', 'L''Hospitalet de Llobregat'), (32, 'Cataluña', 'Terrassa'), (32, 'Cataluña', 'Badalona'), (32, 'Cataluña', 'Sabadell'), (32, 'Cataluña', 'Girona'), (32, 'Cataluña', 'Tarragona'), (32, 'Cataluña', 'Lleida'),
(32, 'Andalucía', 'Sevilla'), (32, 'Andalucía', 'Málaga'), (32, 'Andalucía', 'Córdoba'), (32, 'Andalucía', 'Granada'), (32, 'Andalucía', 'Jerez de la Frontera'), (32, 'Andalucía', 'Almería'), (32, 'Andalucía', 'Marbella'),
(32, 'Comunidad Valenciana', 'Valencia'), (32, 'Comunidad Valenciana', 'Alicante'), (32, 'Comunidad Valenciana', 'Elche'), (32, 'Comunidad Valenciana', 'Castellón de la Plana'),
(32, 'Galicia', 'Vigo'), (32, 'Galicia', 'A Coruña'), (32, 'Galicia', 'Ourense'), (32, 'Galicia', 'Santiago de Compostela'),
(32, 'País Vasco', 'Bilbao'), (32, 'País Vasco', 'Vitoria-Gasteiz'), (32, 'País Vasco', 'San Sebastián'),
(32, 'Canarias', 'Las Palmas de Gran Canaria'), (32, 'Canarias', 'Santa Cruz de Tenerife'), (32, 'Islas Baleares', 'Palma de Mallorca'), (32, 'Islas Baleares', 'Ibiza'),
(32, 'Aragón', 'Zaragoza'), (32, 'Asturias', 'Gijón'), (32, 'Murcia', 'Murcia'), (32, 'Murcia', 'Cartagena')//

-- FINLANDIA (ID 34)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(34, 'Uusimaa', 'Helsinki'), (34, 'Uusimaa', 'Espoo'), (34, 'Pirkanmaa', 'Tampere')//

-- FRANCIA (ID 35)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(35, 'Île-de-France', 'Paris'), (35, 'Île-de-France', 'Boulogne-Billancourt'), (35, 'Île-de-France', 'Saint-Denis'), (35, 'Île-de-France', 'Versailles'),
(35, 'Provence-Alpes-Côte d''Azur', 'Marseille'), (35, 'Provence-Alpes-Côte d''Azur', 'Nice'), (35, 'Provence-Alpes-Côte d''Azur', 'Toulon'),
(35, 'Auvergne-Rhône-Alpes', 'Lyon'), (35, 'Auvergne-Rhône-Alpes', 'Grenoble'), (35, 'Auvergne-Rhône-Alpes', 'Saint-Étienne'), (35, 'Auvergne-Rhône-Alpes', 'Annecy'),
(35, 'Nouvelle-Aquitaine', 'Bordeaux'), (35, 'Nouvelle-Aquitaine', 'Limoges'), (35, 'Occitanie', 'Toulouse'), (35, 'Occitanie', 'Montpellier')//

-- GRECIA (ID 36)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(36, 'Attica', 'Athens'), (36, 'Attica', 'Piraeus'), (36, 'Central Macedonia', 'Thessaloniki'), (36, 'Crete', 'Heraklion')//

-- HUNGRÍA (ID 37)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(37, 'Budapest', 'Budapest'), (37, 'Hajdú-Bihar', 'Debrecen'), (37, 'Csongrád-Csanád', 'Szeged')//

-- ITALIA (ID 40)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(40, 'Lazio', 'Roma'), (40, 'Lombardía', 'Milano'), (40, 'Lombardía', 'Brescia'), (40, 'Lombardía', 'Bergamo'),
(40, 'Campania', 'Napoli'), (40, 'Campania', 'Salerno'), (40, 'Piamonte', 'Torino'), (40, 'Piamonte', 'Novara'),
(40, 'Véneto', 'Venezia'), (40, 'Véneto', 'Verona'), (40, 'Véneto', 'Padova'), (40, 'Sicilia', 'Palermo'), (40, 'Sicilia', 'Catania'),
(40, 'Emilia-Romaña', 'Bologna'), (40, 'Emilia-Romaña', 'Parma'), (40, 'Toscana', 'Firenze'), (40, 'Toscana', 'Prato'), (40, 'Toscana', 'Livorno'),
(40, 'Puglia', 'Bari'), (40, 'Liguria', 'Genova')//

-- NORUEGA (ID 45)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(45, 'Oslo', 'Oslo'), (45, 'Vestland', 'Bergen'), (45, 'Trøndelag', 'Trondheim')//

-- PAÍSES BAJOS (ID 46)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(46, 'Noord-Holland', 'Amsterdam'), (46, 'Noord-Holland', 'Haarlem'), (46, 'Zuid-Holland', 'Rotterdam'), (46, 'Zuid-Holland', 'Den Haag'), (46, 'Zuid-Holland', 'Leiden'), (46, 'Utrecht', 'Utrecht'), (46, 'Noord-Brabant', 'Eindhoven')//

-- POLONIA (ID 47)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(47, 'Masovia', 'Warszawa'), (47, 'Lesser Poland', 'Kraków'), (47, 'Łódź', 'Łódź'), (47, 'Lower Silesia', 'Wrocław'), (47, 'Greater Poland', 'Poznań')//

-- PORTUGAL (ID 48)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(48, 'Lisboa', 'Lisboa'), (48, 'Lisboa', 'Sintra'), (48, 'Lisboa', 'Cascais'), (48, 'Porto', 'Porto'), (48, 'Porto', 'Vila Nova de Gaia'), (48, 'Braga', 'Braga'), (48, 'Faro', 'Faro'), (48, 'Madeira', 'Funchal')//

-- REINO UNIDO (ID 49)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(49, 'England', 'London'), (49, 'England', 'Birmingham'), (49, 'England', 'Manchester'), (49, 'England', 'Leeds'), (49, 'England', 'Sheffield'), (49, 'England', 'Liverpool'), (49, 'England', 'Bristol'), (49, 'England', 'Newcastle upon Tyne'), (49, 'England', 'Nottingham'),
(49, 'Scotland', 'Glasgow'), (49, 'Scotland', 'Edinburgh'), (49, 'Scotland', 'Aberdeen'), (49, 'Scotland', 'Dundee'),
(49, 'Wales', 'Cardiff'), (49, 'Wales', 'Swansea'), (49, 'Wales', 'Newport'),
(49, 'Northern Ireland', 'Belfast'), (49, 'Northern Ireland', 'Derry')//

-- REP. CHECA (ID 50)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(50, 'Prague', 'Prague'), (50, 'South Moravian', 'Brno'), (50, 'Moravian-Silesian', 'Ostrava')//

-- RUMANIA (ID 51)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(51, 'Bucharest', 'Bucharest'), (51, 'Cluj', 'Cluj-Napoca'), (51, 'Timiș', 'Timișoara'), (51, 'Iași', 'Iași')//

-- SUECIA (ID 53)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(53, 'Stockholm', 'Stockholm'), (53, 'Västra Götaland', 'Göteborg'), (53, 'Skåne', 'Malmö')//

-- SUIZA (ID 54)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(54, 'Zürich', 'Zürich'), (54, 'Zürich', 'Winterthur'), (54, 'Genève', 'Genève'), (54, 'Basel-Stadt', 'Basel'), (54, 'Vaud', 'Lausanne'), (54, 'Bern', 'Bern')//

-- ASIA & MEDIO ORIENTE (IDs 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 68, 70, 71)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(56, 'Riyadh', 'Riyadh'), (56, 'Makkah', 'Jeddah'), (56, 'Makkah', 'Mecca'), (56, 'Al Madinah', 'Medina'), (56, 'Eastern Province', 'Dammam'),
(57, 'Beijing', 'Beijing'), (57, 'Shanghai', 'Shanghai'), (57, 'Guangdong', 'Guangzhou'), (57, 'Guangdong', 'Shenzhen'), (57, 'Sichuan', 'Chengdu'), (57, 'Zhejiang', 'Hangzhou'), (57, 'Hubei', 'Wuhan'),
(58, 'Seoul', 'Seoul'), (58, 'Busan', 'Busan'), (58, 'Incheon', 'Incheon'), (58, 'Daegu', 'Daegu'), (58, 'Daejeon', 'Daejeon'), (58, 'Gwangju', 'Gwangju'),
(59, 'Dubai', 'Dubai'), (59, 'Abu Dhabi', 'Abu Dhabi'), (59, 'Sharjah', 'Sharjah'), (59, 'Ajman', 'Ajman'),
(60, 'Metro Manila', 'Manila'), (60, 'Metro Manila', 'Quezon City'), (60, 'Metro Manila', 'Makati'), (60, 'Cebu', 'Cebu City'), (60, 'Davao del Sur', 'Davao City'),
(61, 'Maharashtra', 'Mumbai'), (61, 'Maharashtra', 'Pune'), (61, 'Delhi', 'New Delhi'), (61, 'Karnataka', 'Bengaluru'), (61, 'West Bengal', 'Kolkata'), (61, 'Tamil Nadu', 'Chennai'), (61, 'Telangana', 'Hyderabad'), (61, 'Gujarat', 'Ahmedabad'),
(62, 'Jakarta', 'Jakarta'), (62, 'East Java', 'Surabaya'), (62, 'West Java', 'Bandung'), (62, 'North Sumatra', 'Medan'), (62, 'Bali', 'Denpasar'),
(63, 'Tel Aviv', 'Tel Aviv-Yafo'), (63, 'Jerusalem', 'Jerusalem'), (63, 'Haifa', 'Haifa'), (63, 'Center District', 'Rishon LeZion'),
(64, 'Tokyo', 'Tokyo'), (64, 'Kanagawa', 'Yokohama'), (64, 'Kanagawa', 'Kawasaki'), (64, 'Osaka', 'Osaka'), (64, 'Osaka', 'Sakai'), (64, 'Aichi', 'Nagoya'), (64, 'Hokkaido', 'Sapporo'), (64, 'Fukuoka', 'Fukuoka'), (64, 'Hyogo', 'Kobe'), (64, 'Kyoto', 'Kyoto'),
(65, 'Kuala Lumpur', 'Kuala Lumpur'), (65, 'Selangor', 'Petaling Jaya'), (65, 'Penang', 'George Town'), (65, 'Johor', 'Johor Bahru'),
(68, 'Bangkok', 'Bangkok'), (68, 'Chiang Mai', 'Chiang Mai'), (68, 'Phuket', 'Phuket City'), (68, 'Chonburi', 'Pattaya'),
(70, 'Istanbul', 'Istanbul'), (70, 'Ankara', 'Ankara'), (70, 'Izmir', 'Izmir'), (70, 'Bursa', 'Bursa'), (70, 'Antalya', 'Antalya'),
(71, 'Ho Chi Minh', 'Ho Chi Minh City'), (71, 'Hanoi', 'Hanoi'), (71, 'Da Nang', 'Da Nang'), (71, 'Hai Phong', 'Hai Phong')//

-- OCEANÍA & ÁFRICA (IDs 72, 73, 74, 75, 76, 77, 78)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES
(72, 'New South Wales', 'Sydney'), (72, 'New South Wales', 'Newcastle'), (72, 'New South Wales', 'Wollongong'), (72, 'Victoria', 'Melbourne'), (72, 'Victoria', 'Geelong'), (72, 'Victoria', 'Ballarat'), (72, 'Queensland', 'Brisbane'), (72, 'Queensland', 'Gold Coast'), (72, 'Queensland', 'Cairns'), (72, 'Western Australia', 'Perth'), (72, 'Western Australia', 'Fremantle'), (72, 'South Australia', 'Adelaide'), (72, 'Tasmania', 'Hobart'), (72, 'Australian Capital Territory', 'Canberra'),
(73, 'Cairo', 'Cairo'), (73, 'Alexandria', 'Alexandria'), (73, 'Giza', 'Giza'), (73, 'Port Said', 'Port Said'), (73, 'Suez', 'Suez'),
(74, 'Nairobi', 'Nairobi'), (74, 'Mombasa', 'Mombasa'), (74, 'Kisumu', 'Kisumu'),
(75, 'Casablanca-Settat', 'Casablanca'), (75, 'Rabat-Salé-Kénitra', 'Rabat'), (75, 'Marrakech-Safi', 'Marrakech'), (75, 'Fès-Meknès', 'Fès'), (75, 'Tanger-Tetouan-Al Hoceima', 'Tangier'),
(76, 'Lagos', 'Lagos'), (76, 'Kano', 'Kano'), (76, 'Oyo', 'Ibadan'), (76, 'FCT', 'Abuja'), (76, 'Rivers', 'Port Harcourt'),
(77, 'Auckland', 'Auckland'), (77, 'Wellington', 'Wellington'), (77, 'Canterbury', 'Christchurch'), (77, 'Waikato', 'Hamilton'),
(78, 'Gauteng', 'Johannesburg'), (78, 'Gauteng', 'Pretoria'), (78, 'Western Cape', 'Cape Town'), (78, 'KwaZulu-Natal', 'Durban'), (78, 'Eastern Cape', 'Gqeberha')//

-- ======================================================
-- 5. UBICACIONES FALTANTES (PAÍSES SIN REGISTROS PREVIOS)
-- ======================================================

-- GUYANA (ID 7)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(7, 'Demerara-Mahaica', 'Georgetown')//

-- SURINAM (ID 10)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(10, 'Paramaribo', 'Paramaribo')//

-- CUBA (ID 14)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(14, 'La Habana', 'La Habana'), 
(14, 'Santiago de Cuba', 'Santiago de Cuba'), 
(14, 'Camagüey', 'Camagüey')//

-- EL SALVADOR (ID 15)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(15, 'San Salvador', 'San Salvador'), 
(15, 'La Libertad', 'Santa Tecla'), 
(15, 'Santa Ana', 'Santa Ana')//

-- GUATEMALA (ID 16)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(16, 'Guatemala', 'Ciudad de Guatemala'), 
(16, 'Quetzaltenango', 'Quetzaltenango'), 
(16, 'Escuintla', 'Escuintla')//

-- HONDURAS (ID 17)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(17, 'Francisco Morazán', 'Tegucigalpa'), 
(17, 'Cortés', 'San Pedro Sula'), 
(17, 'Atlántida', 'La Ceiba')//

-- NICARAGUA (ID 18)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(18, 'Managua', 'Managua'), 
(18, 'León', 'León'), 
(18, 'Granada', 'Granada')//

-- BÉLGICA (ID 26)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(26, 'Región de Bruselas-Capital', 'Bruselas'), 
(26, 'Amberes', 'Amberes'), 
(26, 'Flandes Oriental', 'Gante')//

-- BULGARIA (ID 27)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(27, 'Sofía-Ciudad', 'Sofía'), 
(27, 'Plovdiv', 'Plovdiv'), 
(27, 'Varna', 'Varna')//

-- CROACIA (ID 28)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(28, 'Ciudad de Zagreb', 'Zagreb'), 
(28, 'Split-Dalmacia', 'Split'), 
(28, 'Primorje-Gorski Kotar', 'Rijeka')//

-- ESLOVAQUIA (ID 30)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(30, 'Bratislava', 'Bratislava'), 
(30, 'Košice', 'Košice'), 
(30, 'Prešov', 'Prešov')//

-- ESLOVENIA (ID 31)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(31, 'Eslovenia Central', 'Liubliana'), 
(31, 'Drava', 'Maribor')//

-- ESTONIA (ID 33)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(33, 'Harju', 'Tallin'), 
(33, 'Tartu', 'Tartu')//

-- IRLANDA (ID 38)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(38, 'Dublín', 'Dublín'), 
(38, 'Cork', 'Cork'), 
(38, 'Galway', 'Galway')//

-- ISLANDIA (ID 39)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(39, 'Región Capital', 'Reikiavik'), 
(39, 'Norðurland eystra', 'Akureyri')//

-- LETONIA (ID 41)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(41, 'Riga', 'Riga'), 
(41, 'Latgale', 'Daugavpils')//

-- LITUANIA (ID 42)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(42, 'Vilna', 'Vilna'), 
(42, 'Kaunas', 'Kaunas'), 
(42, 'Klaipėda', 'Klaipėda')//

-- LUXEMBURGO (ID 43)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(43, 'Luxemburgo', 'Luxemburgo'), 
(43, 'Esch-sur-Alzette', 'Esch-sur-Alzette')//

-- MALTA (ID 44)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(44, 'Región Sudoriental', 'La Valeta'), 
(44, 'Región Central', 'Birkirkara')//

-- UCRANIA (ID 55)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(55, 'Ciudad de Kiev', 'Kiev'), 
(55, 'Óblast de Járkov', 'Járkov'), 
(55, 'Óblast de Leópolis', 'Leópolis'), 
(55, 'Óblast de Odesa', 'Odesa')//

-- QATAR (ID 66)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(66, 'Ad Dawhah', 'Doha'), 
(66, 'Al Rayyan', 'Al Rayyan')//

-- SINGAPUR (ID 67)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(67, 'Central', 'Singapur')//

-- TAIWÁN (ID 69)
INSERT OR IGNORE INTO locations (country_id, department, municipality) VALUES 
(69, 'Taipéi', 'Taipéi'), 
(69, 'Nueva Taipéi', 'Nueva Taipéi'), 
(69, 'Kaohsiung', 'Kaohsiung')//