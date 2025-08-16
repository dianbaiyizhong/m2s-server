-- 省，市，区
SELECT
    p.name as province_name,
    c.`name` as city_name,
    d.name as distinct_name
FROM
    t_province p
        LEFT JOIN
    t_city c ON p.shorthand = c.province_short_hand
        LEFT JOIN
    t_distinct d ON c.city_code = d.city_code
ORDER BY
    p.name, c.name, d.name;