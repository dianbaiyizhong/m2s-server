-- 省，市，区
SELECT
    country.name as country_name,
    p.name as province_name,
    c.`name` as city_name,
    d.name as distinct_name
FROM
    t_country country
        left JOIN
    t_province p on country.country_code = p.country_code
        LEFT JOIN
    t_city c ON p.shorthand = c.province_short_hand
        LEFT JOIN
    t_distinct d ON c.city_code = d.city_code
where  country.`enable` = 1
ORDER BY
    p.name, c.name, d.name;