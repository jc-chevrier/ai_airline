INSERT INTO GLOBAL (ID) VALUES
(1);

INSERT INTO CITY(ID, NAME, DISTANCE_TO_PARIS, TIME_TO_PARIS) VALUES
(1, 'Paris', 0, 0),
(2, 'New York (Etats-Unis)', 5841.38, 24171),
(3, 'Bangkok (Thaïlande)', 9455.41, 39126),
(4, 'Pointe-à-Pitre (Guadeloupe)', 6759.93, 27972),
(5, 'Fort-de-France (Martinique, France)', 6861.22, 28391),
(6, 'Tokyo (Japon)', 9727.22, 40251),
(7, 'Montréal (Canada)', 5509.04, 22796),
(8, 'Miami (Etats-Unis)', 7361.45, 30461),
(9, 'Los Angeles (Etats-Unis)', 9093.43, 37628),
(10, 'Marrakech (Maroc)', 2104.7, 8709),
(11, 'Saint-Denis (La Réunion, France)', 9376.9, 38801),
(12, 'Lisbonne (Portugal)', 1454.28, 6018),
(13, 'Londres (Royaume-Uni)', 342.53, 1417),
(14, 'Phuket (Thaïlande)', 9792.07, 40519),
(15, 'Rome (Italie)', 1108.48, 1587),
(16, 'Cancún (Mexique)', 8203.56, 33946),
(17, 'Dubaï (Emirats arabe unis)', 5251.71, 21731),
(18, 'Manille (Philippines)', 10757.89, 44515),
(19, 'Porto (Portugal)', 1212.88, 5019),
(20, 'Prague (République tchèque)', 884.94, 3362),
(21, 'San Francisco (Etats-Unis)', 8961.13, 37081),
(22, 'Dublin (Irlande)', 779.84, 3227),
(23, 'San José (Costa Rica)', 8925.13, 36932),
(24, 'Amsterdam (Pays-Bas)', 431.26, 1785),
(25, 'Reykjavik (Islande)', 2234.1, 9245),
(26, 'Port-Louis (Maurice)', 9424.23, 38997),
(27, 'Papeete (Polynésie française, France)', 15727.22, 65078),
(28, 'Dakar (Sénégal)', 4210.27, 17422),
(29, 'Budapest (Hongrie)', 1247.75, 5163),
(30, 'Barcelone (Espagne)', 832.28, 3444);

INSERT INTO PLANE_TYPE(ID, NAME, SALE_PRICE, COUNT_TOTAL_PLACES) VALUES
(1, 'Jet 1', 200000, 10),
(2, 'Jet 2', 400000, 30),
(3, 'Cheap 1', 600000, 70),
(4, 'Cheap 2', 800000, 100),
(5, 'Cargo 1', 1500000, 200),
(6, 'Cargo 2', 1800000, 250);

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