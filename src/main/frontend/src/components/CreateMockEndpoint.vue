<script setup>
const emit = defineEmits(['close'])

const handleCancel = () => {
    emit('close')
}
</script>
<template>
    <div id="view-create-mock">
        <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
            <div class="d-flex align-items-center gap-3">
                <h4 class="fw-bold mb-0 text-dark">Create Mock Endpoint</h4>
            </div>
            <div class="d-flex gap-2">
                <button
                    @click="handleCancel"
                    type="button"
                    class="btn btn-outline-secondary btn-sm">Cancel</button>

                <button
                    type="submit"
                    form="mockForm"
                    class="btn btn-outline-success px-4 fw-bold">
                    Create Mock
                </button>
            </div>
        </div>

        <form id="mockForm">
            <div class="mb-4">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <span class="badge bg-dark rounded-circle p-1 d-flex align-items-center justify-content-center" style="width:20px; height:20px; font-size:10px;">1</span>
                    <label class="form-label fw-bold text-dark mb-0">Request Matching</label>
                </div>
                <div class="bg-white border rounded shadow-sm overflow-hidden">
                    <div class="row g-0">
                        <div class="col-md-2 border-end bg-light-subtle">
                            <select class="form-select border-0 font-mono fw-bold py-3 bg-transparent text-primary text-center">
                                <option>GET</option><option>POST</option><option>PUT</option><option>DELETE</option><option>PATCH</option>
                            </select>
                        </div>
                        <div class="col-md-10">
                            <input type="text" class="form-control border-0 font-mono py-3" placeholder="/api/v1/resource/*/action/*">
                        </div>
                    </div>
                </div>
                <div class="form-text mt-2 text-xs ps-1">Matches incoming requests by method and path. Use * for pattern matching (max 3 patterns per endpoint)</div>
            </div>

            <div class="mb-4">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <span class="badge bg-dark rounded-circle p-1 d-flex align-items-center justify-content-center" style="width:20px; height:20px; font-size:10px;">2</span>
                    <label class="form-label fw-bold text-dark mb-0">Response Headers</label>
                </div>
                <div class="bg-white border rounded shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-sm table-borderless mb-0">
                            <thead class="border-bottom bg-light-subtle">
                            <tr class="text-muted small" style="font-size: 11px; letter-spacing: 0.5px;">
                                <th class="ps-3 py-2 fw-bold">HEADER KEY</th>
                                <th class="py-2 fw-bold">EXPECTED VALUE</th>
                                <th style="width: 50px"></th>
                            </tr>
                            </thead>
                            <tbody class="font-mono border-bottom">
                            <tr>
                                <td class="ps-3 py-2"><input type="text" class="form-control form-control-sm border-0" placeholder="X-Custom-Header"></td>
                                <td class="py-2"><input type="text" class="form-control form-control-sm border-0" placeholder="Custom Header Value"></td>
                                <td class="text-center pt-2"><button class="btn btn-link btn-sm text-danger p-0"><i class="bi bi-x-lg"></i></button></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="p-2 border-top bg-light-subtle">
                        <button type="button" class="btn btn-link btn-sm text-primary text-decoration-none fw-bold small">+ Add Header</button>
                    </div>
                </div>
            </div>

            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-center mb-2 px-1">
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-dark rounded-circle p-1 d-flex align-items-center justify-content-center" style="width:20px; height:20px; font-size:10px;">3</span>
                        <label class="form-label fw-bold text-dark mb-0">Response Body</label>
                    </div>
                    <div class="dropdown">
                        <button class="btn btn-xs btn-outline-secondary border font-mono dropdown-toggle" style="font-size: 11px;" type="button" data-bs-toggle="dropdown">JSON</button>
                        <ul class="dropdown-menu dropdown-menu-end small font-mono">
                            <li><a class="dropdown-item" href="#">JSON</a></li>
                            <li><a class="dropdown-item" href="#">XML</a></li>
                            <li><a class="dropdown-item" href="#">HTML</a></li>
                        </ul>
                    </div>
                </div>
                <div class="border rounded shadow-sm overflow-hidden bg-white">
                    <textarea class="form-control font-mono p-3 border-0 bg-light-subtle text-dark" rows="12" style="font-size: 13px; min-height: 250px; outline: none; resize: vertical;" placeholder='{ "status": "success", "data": {} }'></textarea>
                </div>
            </div>

            <div class="mb-5">
                <div class="d-flex align-items-center gap-2 mb-2 px-1">
                    <span class="badge bg-dark rounded-circle p-1 d-flex align-items-center justify-content-center" style="width:20px; height:20px; font-size:10px;">4</span>
                    <label class="form-label fw-bold text-dark mb-0">Response Settings</label>
                </div>
                <div class="p-4 bg-white border rounded shadow-sm">
                    <div class="row g-4">
                        <div class="col-md-6 border-end">
                            <label class="text-muted small fw-bold mb-2 d-block">HTTP STATUS</label>
                            <select class="form-select border-0 bg-light p-2 font-mono fw-bold">
                                <option value="200">200 OK</option>
                                <option value="201">201 Created</option>
                                <option value="204">204 No Content</option>
                                <option value="400">400 Bad Request</option>
                                <option value="404">404 Not Found</option>
                            </select>
                        </div>
                        <div class="col-md-6 border-end">
                            <label class="text-muted small fw-bold mb-2 d-block">DELAY (MS)</label>
                            <div class="input-group bg-light rounded">
                                <select class="form-select border-0 bg-light p-2 font-mono fw-bold">
                                    <option value="0">No delay</option>
                                    <option value="200">200</option>
                                    <option value="500">500</option>
                                    <option value="1000">1000</option>
                                    <option value="2000">2000</option>
                                    <option value="5000">5000</option>
                                    <option value="10000">10000</option>
                                    <option value="20000">20000</option>
                                    <option value="30000">30000</option>
<!--                                    <option value="60000">60000</option>-->
                                </select>
                                <span class="input-group-text border-0 bg-transparent text-muted font-mono small">ms</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</template>