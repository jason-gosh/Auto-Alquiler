/**
 * AutoAlquiler - Reusable Geographical Cascading Dropdowns & Phone Prefix Handler
 * Handles Country -> Department -> Municipality cascade asynchronously
 * and manages dynamic phone country prefix formatting.
 */
document.addEventListener("DOMContentLoaded", function () {
  // 1. DOM Elements - Location Cascade
  const countrySelect = document.getElementById('countrySelect');
  const departmentSelect = document.getElementById('departmentSelect');
  const municipalitySelect = document.getElementById('municipalitySelect');

  // 2. DOM Elements - Phone Prefix & Input 
  const phonePrefix = document.getElementById('phonePrefix');
  const phoneInput = document.getElementById('phoneInput');

  // Safe Guard for Locations: If the current page doesn't have these inputs, stop cascade execution quietly.
  if (!countrySelect || !departmentSelect || !municipalitySelect) {
      console.warn("⚠️ El script se detuvo porque uno o más selectores de ubicación son NULL.");
      return; 
  }

  // 3. Phone Prefix Formatting Engine (Mantiene tu lógica exacta del signo +)
  function updatePhonePrefix() {
      if (!phonePrefix) return;

      const selectedOption = countrySelect.options[countrySelect.selectedIndex];
      const prefix = selectedOption ? selectedOption.getAttribute("data-prefix") : null;
      
      if (prefix && prefix.trim() !== "") {
          phonePrefix.textContent = `+${prefix.trim()}`;
      } else {
          phonePrefix.textContent = "--";
      }
  }

  // 4. Sanitize Phone Input (Mantiene tu filtro estricto de solo dígitos)
  if (phoneInput) {
      phoneInput.addEventListener('input', function () {
          this.value = this.value.replace(/\D/g, "");
      });
  }

  // Trigger initial prefix load in case the form comes pre-populated from the backend
  updatePhonePrefix();


  // --- 🌟 MOTOR GLOBAL DE RECONSTRUCCIÓN (HYDRATION ENGINE) ---
  // Se expone de forma segura dentro del ciclo de vida para Perfil, Catálogo, Vehículos y Reservas
  window.hydrateLocationCascade = function (config) {
      const { countryId, department, municipality, preloadedDepartments, preloadedMunicipalities } = config;

      // Asignar el país seleccionado y disparar el prefijo
      countrySelect.value = countryId || '';
      updatePhonePrefix();

      // Reconstrucción del segundo nivel (Departamentos)
      if (preloadedDepartments && preloadedDepartments.length > 0) {
          departmentSelect.innerHTML = '<option value="">-- Seleccione un Departamento --</option>';
          preloadedDepartments.forEach(dept => {
              const opt = document.createElement('option');
              opt.value = dept;
              opt.textContent = dept;
              if (dept === department) opt.selected = true;
              departmentSelect.appendChild(opt);
          });
          departmentSelect.disabled = false;
      } else {
          departmentSelect.innerHTML = '<option value="">-- Seleccione un Departamento --</option>';
          departmentSelect.disabled = true;
      }

      // Reconstrucción del tercer nivel (Municipios / Ciudades)
      if (preloadedMunicipalities && preloadedMunicipalities.length > 0) {
          municipalitySelect.innerHTML = '<option value="">-- Seleccione un Municipio --</option>';
          preloadedMunicipalities.forEach(muni => {
              const opt = document.createElement('option');
              opt.value = muni;
              opt.textContent = muni;
              if (muni === municipality) opt.selected = true;
              municipalitySelect.appendChild(opt);
          });
          municipalitySelect.disabled = false;
      } else {
          municipalitySelect.innerHTML = '<option value="">-- Seleccione primero un Departamento --</option>';
          municipalitySelect.disabled = true;
      }
  };


  // --- 🔄 ASYNC INTERACTION LISTENERS (Tus flujos manuales con async/await originales) ---

  // 1. Country Change -> Fetch and Populate Departments & Update Phone Prefix
  countrySelect.addEventListener('change', async function () {
      const countryId = this.value;
      console.log("El ID del país capturado es:", countryId, "Tipo de dato:", typeof countryId);
      
      // Update phone prefix view side-by-side with geography cascading
      updatePhonePrefix();

      // Reset downward cascade strictly
      departmentSelect.innerHTML = '<option value="">-- Seleccione un Departamento --</option>';
      municipalitySelect.innerHTML = '<option value="">-- Seleccione primero un Departamento --</option>';
      departmentSelect.disabled = true;
      municipalitySelect.disabled = true;

      if (!countryId) return;

      try {
          const response = await fetch(`/locations/departments?countryId=${countryId}`);
          if (!response.ok) throw new Error("Fallo al buscar los departamentos en el servidor..");
          
          const departments = await response.json();
          
          departments.forEach(dept => {
              const opt = document.createElement('option');
              opt.value = dept;
              opt.textContent = dept;
              departmentSelect.appendChild(opt);
          });
          
          // Domino Effect: Unlock next tier
          departmentSelect.disabled = false;
      } catch (error) {
          console.error("Error loading departments:", error);
      }
  });

  // 2. Department Change -> Fetch and Populate Municipalities
  departmentSelect.addEventListener('change', async function () {
      const countryId = countrySelect.value;
      const departmentName = encodeURIComponent(this.value);

      // Reset lowest tier
      municipalitySelect.innerHTML = '<option value="">-- Seleccione un Municipio --</option>';
      municipalitySelect.disabled = true;

      if (!this.value) return;

      try {
          const response = await fetch(`/locations/municipalities?countryId=${countryId}&department=${departmentName}`);
          if (!response.ok) throw new Error("Fallo al buscar los municipios en el servidor.");
          
          const municipalities = await response.json();
          
          municipalities.forEach(muni => {
              const opt = document.createElement('option');
              opt.value = muni;
              opt.textContent = muni;
              municipalitySelect.appendChild(opt);
          });
          
          // Domino Effect: Unlock final tier
          municipalitySelect.disabled = false;
       } catch (error) {
          console.error("Error loading municipalities:", error);
       }
  });
});