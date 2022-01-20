INSERT INTO CITY(ID, NAME, DISTANCE_TO_PARIS) VALUES
(1, 'Paris', 0),
(2, 'New York (Etats-Unis)', 5841.38),
(3, 'Bangkok (Thaïlande)', 9455.41),
(4, 'Pointe-à-Pitre (Guadeloupe)', 6759.93),
(5, 'Fort-de-France (Martinique, France)', 6861.22),
(6, 'Tokyo (Japon)', 9727.22),
(7, 'Montréal (Canada)', 5509.04),
(8, 'Miami (Etats-Unis)', 7361.45),
(9, 'Los Angeles (Etats-Unis)', 9093.43),
(10, 'Marrakech (Maroc)', 2104.7),
(11, 'Saint-Denis (La Réunion, France)', 9376.9),
(12, 'Lisbonne (Portugal)', 1454.28),
(13, 'Londres (Royaume-Uni)', 342.53),
(14, 'Phuket (Thaïlande)', 9792.07),
(15, 'Rome (Italie)', 1108.48),
(16, 'Cancún (Mexique)', 8203.56),
(17, 'Dubaï (Emirats arabe unis)', 5251.71),
(18, 'Manille (Philippines)', 10757.89),
(19, 'Porto (Portugal)', 1212.88),
(20, 'Prague (République tchèque)', 884.94),
(21, 'San Francisco (Etats-Unis)', 8961.13),
(22, 'Dublin (Irlande)', 779.84),
(23, 'San José (Costa Rica)', 8925.13),
(24, 'Amsterdam (Pays-Bas)', 431.26),
(25, 'Reykjavik (Islande)', 2234.1),
(26, 'Port-Louis (Maurice)', 9424.23),
(27, 'Papeete (Polynésie française, France)', 15727.22),
(28, 'Dakar (Sénégal)', 4210.27),
(29, 'Budapest (Hongrie)', 1247.75),
(30, 'Barcelone (Espagne)', 832.28);

INSERT INTO PLANE_TYPE(ID, NAME, SALE_PRICE, MAINTENANCE_COST, COUNT_TOTAL_PLACES) VALUES
(1, 'Jet 1', 200000, 1000, 10),
(2, 'Jet 2', 400000, 1300, 30),
(3, 'Cheap 1', 600000, 4000, 70),
(4, 'Cheap 2', 800000, 4500, 100),
(5, 'Cargo 1', 1500000, 8000, 200),
(6, 'Cargo 1', 1800000, 9000, 250);

INSERT INTO PLANE_TYPE_CLASS(ID, NAME, COUNT_TOTAL_PLACES, PLANE_TYPE_ID) VALUES
(1, 'Première', 10, 1),
(2, 'Business', 30, 2),
(3, 'Economique', 70, 3),
(4, 'Economique', 100, 4),
(5, 'Première', 20, 5),
(6, 'Business', 50, 5),
(7, 'Economique', 130, 5),
(8, 'Première', 30, 6),
(9, 'Business', 70, 6),
(10, 'Economique', 150, 6);