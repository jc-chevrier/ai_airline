-- Achat d'avions
INSERT INTO plane (plane_type_id) values(1); -- Jet
INSERT INTO plane (plane_type_id) values(5); -- Gros porteur
-- Vol à venir
-- Paris - N.Y. 30 janvier en Jet
INSERT INTO flight (start_date, arrival_date, floor_price, start_city_id, arrival_city_id, plane_id) values(timestamp '2022-01-30 10:00',timestamp '2022-01-30 18:00',00.00,1,2,1);
-- Avec 20 places en première
INSERT INTO flight_class (floor_place_price, place_price, count_available_places, count_occupied_places, flight_id, plane_type_class_id) values(10.00,100.00,20,20,1,2);
