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


-- 查询所有
SELECT
    id,
    `name`,
    intro,
    country_code AS `code`,
    lat,
    lng,
    image_url AS imageUrl,
    '' AS `parentCode`,
    4 AS `areaLevel`,
    `code` AS `provinceShortHand`,
    '' AS `themeColor`,
    '' AS `ncpSlogan`,
    '' AS `licensePlateNum`
FROM
    t_country
WHERE
    `enable` = 1 UNION
SELECT
    id,
    `name`,
    intro,
    `code`,
    lat,
    lng,
    image_url AS imageUrl,
    country_code AS `parentCode`,
    1 AS `areaLevel`,
    shorthand AS `provinceShortHand`,
    bg_color AS `themeColor`,
    ncp_slogan AS `ncpSlogan`,
    '' AS `licensePlateNum`
FROM
    t_province UNION
SELECT
    id,
    `name`,
    intro,
    city_code AS `code`,
    lat,
    lng,
    image_url AS imageUrl,
    province_code AS `parentCode`,
    2 AS `areaLevel`,
    province_short_hand AS `provinceShortHand`,
    '' AS `themeColor`,
    '' AS `ncpSlogan`,
    license_plate_num AS `licensePlateNum`
FROM
    t_city UNION
SELECT
    id,
    `name`,
    '' AS intro,
    city_code AS `code`,
    lat,
    lng,
    '' AS imageUrl,
    city_code AS `parentCode`,
    3 AS `areaLevel`,
    '' AS `provinceShortHand`,
    '' AS `themeColor`,
    '' AS `ncpSlogan`,
    '' AS `licensePlateNum`
FROM
    t_distinct