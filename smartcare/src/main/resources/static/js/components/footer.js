const footer = document.getElementById("footer");
footer.innerHTML = `...`;

renderFooter();

function renderFooter() {
}

/**
 * You write regular HTML tags (<footer>, <div>, <h4>,<a>) as part of the string. - Top-level container: <footer class="footer"> wraps the whole thing. - Branding section: ```js


© Copyright ...

``` - Link sections divided into 3 columns: - Company (About, Careers, Press) - Support (Account, Help Center, Contact) - Legals (Terms, Privacy Policy, Licensing) Each column uses a <div class="footer-column"> with a heading and anchor tags.
 */